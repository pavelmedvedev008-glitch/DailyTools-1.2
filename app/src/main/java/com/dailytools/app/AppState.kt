package com.dailytools.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Всё состояние макета хранится в памяти. После закрытия приложения сбрасывается. */
class AppState {
    val ads = mutableStateListOf<Ad>().also { it.addAll(sampleAds()) }
    val favorites = mutableStateListOf<Int>()
    val tenders = mutableStateListOf<Tender>().also { it.addAll(sampleTenders()) }
    val contractors = mutableStateListOf<Contractor>().also { it.addAll(sampleContractors()) }
    val notifications = mutableStateListOf<AppNotification>()

    var city by mutableStateOf(DEFAULT_CITY)
    var query by mutableStateOf("")
    var loggedIn by mutableStateOf(false)

    val userName = "Пользователь"
    val userPhone = "+7 900 123-45-67"

    fun cityAds(): List<Ad> = ads.filter { it.city == city }.sortedBy { it.daysAgo }

    fun search(): List<Ad> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return cityAds()
        val words = q.split(" ").filter { it.isNotEmpty() }
        return cityAds().filter { ad ->
            val hay = (ad.title + " " + ad.description + " " + categoryOf(ad.categoryId).title).lowercase()
            words.all { hay.contains(it) }
        }
    }

    fun isFav(id: Int) = favorites.contains(id)

    fun toggleFav(id: Int) {
        if (!favorites.remove(id)) favorites.add(id)
    }

    fun nextId(): Int = (ads.maxOfOrNull { it.id } ?: 0) + 1

    fun addAd(ad: Ad) {
        ads.add(0, ad)
    }

    // ---------------- подрядчики и тендеры

    val me: Contractor? get() = contractors.firstOrNull { it.mine }
    val unread: Int get() = notifications.count { !it.read }

    fun cityTenders(): List<Tender> = tenders.filter { it.city == city }.sortedBy { it.daysAgo }

    fun cityContractors(): List<Contractor> = contractors.filter { it.city == city }

    fun nextTenderId(): Int = (tenders.maxOfOrNull { it.id } ?: 0) + 1
    fun nextContractorId(): Int = (contractors.maxOfOrNull { it.id } ?: 0) + 1

    /**
     * Публикует тендер. Возвращает, скольким подрядчикам-юрлицам города «ушло уведомление».
     * В макете реальных уведомлений нет: показывается только число получателей.
     */
    fun createTender(t: Tender): Int {
        tenders.add(0, t)
        return contractors.count { it.kind.legal && !it.mine && it.city == t.city }
    }

    /** Регистрация подрядчика. Юрлицам сразу приходит тестовое уведомление о тендере. */
    fun registerContractor(c: Contractor) {
        contractors.add(0, c)
        if (c.kind.legal) {
            notifications.add(0, AppNotification(
                "Вы зарегистрированы как ${c.kind.title}",
                "Теперь вам будут приходить уведомления о новых тендерах в г. ${c.city}."
            ))
            val t = tenders.firstOrNull { it.city == c.city && !it.mine }
            if (t != null) {
                notifications.add(0, AppNotification(
                    "Новый тендер: ${sectionOf(t.section).title}",
                    t.title, t.id
                ))
            }
        }
    }
}
