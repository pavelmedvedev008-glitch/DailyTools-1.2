package com.dailytools.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// ---------------------------------------------------------------- подрядчики

enum class ContractorKind(val title: String, val plural: String, val color: Color) {
    PERSON("Физлицо", "Физлица", Color(0xFF607D8B)),
    SELF("Самозанятый", "Самозанятые", Color(0xFF00897B)),
    IP("ИП", "ИП", Color(0xFFFF8F00)),
    OOO("ООО", "ООО", Color(0xFF3949AB));

    /** Юрлица и предприниматели: им приходят уведомления о новых тендерах. */
    val legal: Boolean get() = this != PERSON
}

val contractorServices = listOf(
    "Ремонт квартир", "Отделка", "Электрика", "Сантехника", "Строительство домов",
    "Фундамент", "Кровля", "Земляные работы", "Аренда спецтехники", "Демонтаж",
    "Сварочные работы", "Кладка", "Фасады", "Ландшафт"
)

class Contractor(
    val id: Int,
    val name: String,
    val kind: ContractorKind,
    val city: String,
    val services: List<String>,
    val experience: Int,
    val about: String,
    val phone: String,
    val rating: Float,
    val jobs: Int,
    val inn: String = "",
    val mine: Boolean = false
)

fun sampleContractors(): List<Contractor> = listOf(
    Contractor(1, "Андрей Петров", ContractorKind.SELF, "Анапа", listOf("Ремонт квартир", "Отделка"), 9,
        "Делаю ремонт квартир под ключ: черновая и чистовая отделка, выравнивание стен, плитка. Работаю один, без посредников.", "+7 918 201-11-45", 4.9f, 63),
    Contractor(2, "Мастер Плюс", ContractorKind.IP, "Анапа", listOf("Электрика", "Сантехника"), 12,
        "Электромонтаж и сантехника в квартирах и домах. Выезд в день обращения, гарантия на работы 2 года.", "+7 918 305-20-18", 4.8f, 214),
    Contractor(3, "СтройЮг", ContractorKind.OOO, "Анапа", listOf("Строительство домов", "Фундамент", "Кровля"), 15,
        "Строим дома из газобетона и кирпича. Проект, смета, договор, этапы оплаты. Своя бригада и техника.", "+7 861 555-30-30", 4.7f, 48),
    Contractor(4, "Виктор Н.", ContractorKind.PERSON, "Анапа", listOf("Демонтаж"), 4,
        "Демонтаж стен, полов, плитки. Вывоз мусора.", "+7 918 411-90-02", 4.5f, 17),
    Contractor(5, "Дмитрий Орлов", ContractorKind.SELF, "Анапа", listOf("Сварочные работы", "Кладка"), 11,
        "Сварка заборов, навесов, ворот, лестниц. Кладка забора и стен.", "+7 918 622-14-77", 4.9f, 95),
    Contractor(6, "АнапаСтройСервис", ContractorKind.OOO, "Анапа", listOf("Ремонт квартир", "Отделка", "Фасады"), 8,
        "Ремонт квартир и коммерческих помещений, фасадные работы. Работаем по договору с гарантией.", "+7 861 777-45-45", 4.6f, 131),
    Contractor(7, "ИП Соколов А. В.", ContractorKind.IP, "Анапа", listOf("Кровля", "Фасады"), 10,
        "Монтаж кровли любой сложности, утепление, водостоки.", "+7 918 818-60-60", 4.8f, 72),
    Contractor(8, "Спецтех-Юг", ContractorKind.OOO, "Анапа", listOf("Аренда спецтехники", "Земляные работы"), 14,
        "Экскаваторы, самосвалы, катки. Земляные работы любого объёма.", "+7 861 900-10-10", 4.9f, 302),
    Contractor(9, "Краснодар-Ремонт", ContractorKind.OOO, "Краснодар", listOf("Ремонт квартир", "Отделка"), 7,
        "Ремонт квартир в новостройках, дизайн-проект.", "+7 861 222-33-44", 4.5f, 88),
    Contractor(10, "Олег Мастеров", ContractorKind.SELF, "Краснодар", listOf("Электрика"), 6,
        "Электрика в квартирах и офисах.", "+7 918 700-00-15", 4.7f, 54),
    Contractor(11, "Сочи-Строй", ContractorKind.IP, "Сочи", listOf("Строительство домов", "Ландшафт"), 9,
        "Дома, беседки, благоустройство участков.", "+7 862 333-22-11", 4.6f, 39)
)

// ---------------------------------------------------------------- тендеры

class TenderSection(val id: String, val title: String, val icon: ImageVector, val color: Color)

val tenderSections = listOf(
    TenderSection("repair", "Ремонт", Icons.Default.Handyman, Color(0xFFFF8F00)),
    TenderSection("buy", "Покупка", Icons.Default.ShoppingCart, Color(0xFF43A047)),
    TenderSection("rent", "Аренда", Icons.Default.Key, Color(0xFF1E88E5)),
    TenderSection("build", "Строительство", Icons.Default.Domain, Red),
    TenderSection("heavy", "Спецтехника", Icons.Default.Agriculture, Color(0xFF8E24AA))
)

fun sectionOf(id: String): TenderSection = tenderSections.firstOrNull { it.id == id } ?: tenderSections.first()

val deadlines = listOf("Срочно", "В течение недели", "В течение месяца", "Не срочно")

class Offer(val from: String, val price: Int, val comment: String)

class Tender(
    val id: Int,
    val section: String,
    val title: String,
    val description: String,
    val place: String,
    val city: String,
    val budget: Int?,        // null = договорной
    val deadline: String,
    val author: String,
    val daysAgo: Int,
    val mine: Boolean = false,
    val offers: SnapshotStateList<Offer> = mutableStateListOf()
)

fun sampleTenders(): List<Tender> = listOf(
    Tender(1, "repair", "Ремонт двухкомнатной квартиры под ключ",
        "Квартира 62 м² в новостройке, чистовая отделка. Нужны выравнивание стен, электрика, плитка в санузле, натяжные потолки, ламинат. Материалы частично куплены.",
        "г. Анапа, ул. Крымская", "Анапа", 450_000, "В течение месяца", "Ольга", 1,
        offers = mutableStateListOf(
            Offer("Мастер Плюс", 430_000, "Выйдем на объект на следующей неделе, смета бесплатно."),
            Offer("АнапаСтройСервис", 465_000, "Гарантия 3 года, работаем по договору.")
        )),
    Tender(2, "build", "Построить дом 120 м² из газобетона",
        "Участок 6 соток, фундамент не заливали. Нужен проект и строительство под крышу. Рассмотрим этапную оплату.",
        "пос. Витязево", "Анапа", 4_200_000, "Не срочно", "Сергей", 2),
    Tender(3, "heavy", "Нужен экскаватор на 3 дня",
        "Копка котлована под фундамент 10×12 м, глубина 1,5 м. Нужен экскаватор с оператором и самосвал для вывоза грунта.",
        "ст. Гостагаевская", "Анапа", 45_000, "Срочно", "Игорь", 0),
    Tender(4, "rent", "Арендовать строительные леса на 2 недели",
        "Нужны рамные леса на фасад частного дома, 60 м². Доставка желательна.",
        "г. Анапа, Пионерский пр-т", "Анапа", 12_000, "В течение недели", "Максим", 3),
    Tender(5, "buy", "Куплю бетономешалку и виброплиту",
        "Рассмотрю б/у в хорошем состоянии. Самовывоз по Анапе.",
        "г. Анапа", "Анапа", 25_000, "Не срочно", "Артём", 4),
    Tender(6, "repair", "Ремонт ванной комнаты",
        "Ванная 4 м²: демонтаж старой плитки, гидроизоляция, новая плитка, установка сантехники.",
        "г. Анапа, ул. Терская", "Анапа", null, "В течение месяца", "Елена", 5),
    Tender(7, "build", "Построить гараж 6×8 м",
        "Каркасный или блочный гараж с воротами.", "г. Краснодар", "Краснодар", 700_000, "Не срочно", "Роман", 2)
)

class AppNotification(val title: String, val text: String, val tenderId: Int? = null) {
    var read by mutableStateOf(false)
}

fun money(n: Int): String = "%,d".format(java.util.Locale.US, n).replace(',', ' ') + " ₽"
