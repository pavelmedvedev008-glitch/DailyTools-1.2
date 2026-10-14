package com.dailytools.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.ElectricalServices
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Hardware
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.ui.graphics.vector.ImageVector

enum class DealType { SALE, RENT }

data class ToolCategory(val id: String, val title: String, val icon: ImageVector)

data class Ad(
    val id: Int,
    val title: String,
    val price: Int,
    val type: DealType,
    val categoryId: String,
    val city: String,
    val description: String,
    val seller: String,
    val phone: String,
    val daysAgo: Int,
    val condition: String,
    val mapX: Float,
    val mapY: Float,
    val mine: Boolean = false
)

const val DEFAULT_CITY = "Анапа"

val cities = listOf("Анапа", "Краснодар", "Новороссийск", "Сочи", "Москва", "Санкт-Петербург")

val categories = listOf(
    ToolCategory("power", "Электро-инструмент", Icons.Default.Construction),
    ToolCategory("hand", "Ручной инструмент", Icons.Default.Handyman),
    ToolCategory("heavy", "Спецтехника", Icons.Default.Agriculture),
    ToolCategory("lift", "Подъёмники и леса", Icons.Default.PrecisionManufacturing),
    ToolCategory("weld", "Сварка", Icons.Default.ElectricalServices),
    ToolCategory("measure", "Измерительный", Icons.Default.Straighten),
    ToolCategory("paint", "Малярный и отделка", Icons.Default.FormatPaint),
    ToolCategory("plumb", "Сантехнический", Icons.Default.Plumbing),
    ToolCategory("gen", "Генераторы, компрессоры", Icons.Default.Bolt),
    ToolCategory("truck", "Грузовой транспорт", Icons.Default.LocalShipping),
    ToolCategory("wood", "Столярный", Icons.Default.Carpenter),
    ToolCategory("mat", "Крепёж и расходники", Icons.Default.Hardware)
)

fun categoryOf(id: String): ToolCategory = categories.firstOrNull { it.id == id } ?: categories.first()

private var seq = 1

private fun ad(
    title: String,
    price: Int,
    type: DealType,
    cat: String,
    seller: String,
    cond: String,
    days: Int,
    desc: String,
    city: String = DEFAULT_CITY,
    mine: Boolean = false
): Ad {
    val id = seq++
    return Ad(
        id = id,
        title = title,
        price = price,
        type = type,
        categoryId = cat,
        city = city,
        description = desc,
        seller = seller,
        phone = "+7 918 ${100 + (id * 13) % 900}-${10 + id % 80}-${10 + (id * 3) % 80}",
        daysAgo = days,
        condition = cond,
        mapX = 0.22f + ((id * 37) % 62) / 100f,
        mapY = 0.08f + ((id * 53) % 72) / 100f,
        mine = mine
    )
}

fun sampleAds(): List<Ad> {
    val s = DealType.SALE
    val r = DealType.RENT
    return listOf(
        ad("Перфоратор Makita HR2470, 780 Вт", 7500, s, "power", "Андрей", "Б/у", 0,
            "Рабочий, в комплекте кейс и три бура. Пользовался для ремонта в квартире, продаю из-за ненадобности."),
        ad("Перфоратор Bosch GBH 2-26 в аренду", 600, r, "power", "Сергей", "Хорошее", 0,
            "Сдаю на сутки. Залог 3000 ₽, документ обязателен. Бур в комплекте."),
        ad("Шуруповёрт DeWalt 18 В, 2 аккумулятора", 11900, s, "power", "Максим", "Новое", 1,
            "Новый, в упаковке, гарантия магазина. Два аккумулятора 2 А·ч и зарядное устройство."),
        ad("Болгарка 125 мм Hitachi", 3200, s, "power", "Игорь", "Б/у", 2,
            "Работает отлично, новая щётка. Диск в подарок."),
        ad("Бетономешалка 180 л", 14500, s, "heavy", "Виктор", "Б/у", 1,
            "Бетономешалка 220 В, венец чугунный. Самовывоз, помогу погрузить."),
        ad("Бетономешалка в аренду, 180 л", 700, r, "heavy", "Виктор", "Хорошее", 3,
            "Сдаю посуточно. Привезу и заберу за доплату."),
        ad("Мини-экскаватор Kubota, аренда с оператором", 18000, r, "heavy", "Строй-Юг", "Хорошее", 1,
            "Копка траншей, котлованов, планировка участка. Цена за смену 8 часов, с оператором."),
        ad("Мини-погрузчик Bobcat S130", 1650000, s, "heavy", "Артём", "Б/у", 5,
            "2016 год, наработка 3200 моточасов. Обслужен, готов к работе. Торг уместен."),
        ad("Строительные леса рамные, 40 м²", 28000, s, "lift", "Олег", "Б/у", 4,
            "Комплект рамных лесов: рамы, связи, настилы. Хватит на двухэтажный дом."),
        ad("Вышка-тура аренда 6 м", 900, r, "lift", "Олег", "Хорошее", 6,
            "Передвижная вышка-тура, высота рабочей площадки до 6 метров. Сдаю по суткам."),
        ad("Сварочный инвертор Ресанта 220А", 6900, s, "weld", "Дмитрий", "Новое", 2,
            "Новый, в коробке. Электроды 3 мм в комплекте, маска хамелеон."),
        ad("Сварочный полуавтомат в аренду", 1200, r, "weld", "Руслан", "Хорошее", 7,
            "Полуавтомат MIG/MAG, баллон и проволока по запросу. Залог по договорённости."),
        ad("Лазерный уровень Bosch GLL 3-80", 13500, s, "measure", "Антон", "Б/у", 3,
            "Три плоскости, зелёный луч, штатив и кейс. Калибровка проверена."),
        ad("Нивелир оптический + рейка", 5200, s, "measure", "Николай", "Б/у", 9,
            "Рабочий нивелир со штативом и рейкой 4 м."),
        ad("Покрасочный аппарат Wagner, аренда", 1500, r, "paint", "Елена", "Хорошее", 2,
            "Безвоздушный окрасочный аппарат, подойдёт для фасадов и больших поверхностей."),
        ad("Шлифмашина по штукатурке «жираф»", 4800, s, "paint", "Павел", "Б/у", 8,
            "Лёгкая, с пылесосом в комплекте. Пользовался один объект."),
        ad("Опрессовщик для труб, ручной", 9800, s, "plumb", "Константин", "Новое", 10,
            "Ручной опрессовщик 0–60 бар, манометр, шланги. Новый."),
        ad("Бензиновый генератор 5 кВт", 32000, s, "gen", "Ильдар", "Б/у", 1,
            "Запуск электростартером, наработка небольшая. Подойдёт для дачи и стройки."),
        ad("Генератор 3 кВт в аренду", 1000, r, "gen", "Ильдар", "Хорошее", 4,
            "Сдаю посуточно, топливо заправлено. Выдаю с инструкцией."),
        ad("Компрессор 100 л, 2 цилиндра", 15800, s, "gen", "Вадим", "Б/у", 6,
            "Мощный, ременной привод. Для покраски и пневмоинструмента."),
        ad("Газель бортовая, аренда с водителем", 3500, r, "truck", "Алексей", "Хорошее", 0,
            "Доставка стройматериалов по городу и краю. Цена за час, минимум 2 часа."),
        ad("Торцовочная пила Metabo 216 мм", 12400, s, "wood", "Роман", "Б/у", 5,
            "Торцовочная пила с протяжкой, пильный диск новый. Состояние отличное."),
        ad("Набор бит и свёрл, 120 предметов", 1900, s, "hand", "Антон", "Новое", 3,
            "Универсальный набор в кейсе. Подойдёт и для дома, и для объекта."),
        ad("Саморезы и дюбели, оптом", 4500, s, "mat", "Магазин «Крепёж»", "Новое", 2,
            "Саморезы по дереву и металлу, дюбели, анкера. Оптом дешевле, доставка по городу."),
        // мои объявления
        ad("Лобзик электрический Bosch", 3900, s, "power", "Пользователь", "Б/у", 11,
            "Рабочий, с набором пилок.", mine = true),
        ad("Лестница-трансформер 4×3 в аренду", 400, r, "hand", "Пользователь", "Хорошее", 12,
            "Алюминиевая, выдерживает до 150 кг.", mine = true),
        // другие города
        ad("Виброплита 90 кг, аренда", 1800, r, "heavy", "Краснодар-Строй", "Хорошее", 1,
            "Бензиновая виброплита для трамбовки.", city = "Краснодар"),
        ad("Набор ручного инструмента 150 предметов", 5600, s, "hand", "Борис", "Новое", 2,
            "Профессиональный набор в кейсе.", city = "Краснодар"),
        ad("Отбойный молоток Hilti TE 1000", 38000, s, "power", "Леонид", "Б/у", 3,
            "Мощный, рабочий. Два зубила в комплекте.", city = "Новороссийск"),
        ad("Автовышка 18 м, аренда", 2500, r, "lift", "ВысотаПро", "Хорошее", 2,
            "Работаем по Сочи и Адлеру. Цена за час.", city = "Сочи"),
        ad("Тепловая пушка дизельная 30 кВт", 21000, s, "gen", "Игорь", "Б/у", 4,
            "Для обогрева стройки зимой.", city = "Москва"),
        ad("Лазерный дальномер Leica", 17500, s, "measure", "Пётр", "Б/у", 6,
            "Точный, с чехлом.", city = "Санкт-Петербург")
    )
}
