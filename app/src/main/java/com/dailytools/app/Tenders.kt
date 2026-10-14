@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.dailytools.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun Back(onBack: () -> Unit) {
    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") }
}

@Composable
private fun topColors() = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)

@Composable
private fun colorChip(color: Color) = androidx.compose.material3.FilterChipDefaults.filterChipColors(
    selectedContainerColor = color,
    selectedLabelColor = Color.White
)

@Composable
fun Tag(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(color = color.copy(alpha = 0.14f), contentColor = color, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Text(
            text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// ---------------------------------------------------------------- Тендеры: список

@Composable
fun TendersScreen(state: AppState, onBack: () -> Unit, onOpen: (Int) -> Unit, onCreate: () -> Unit) {
    var section by remember { mutableStateOf<String?>(null) }
    val list = state.cityTenders().filter { section == null || it.section == section }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Тендеры", fontWeight = FontWeight.Bold) },
                navigationIcon = { Back(onBack) },
                colors = topColors()
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreate,
                containerColor = Red,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Создать тендер", fontWeight = FontWeight.Bold) }
            )
        }
    ) { inner ->
        LazyColumn(
            Modifier.padding(inner).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            item {
                Text(
                    "Заказы в г. ${state.city}",
                    fontSize = 14.sp, color = TextGrey,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = section == null, onClick = { section = null },
                            label = { Text("Все") }, colors = dealChipColors()
                        )
                    }
                    items(tenderSections, key = { it.id }) { sec ->
                        FilterChip(
                            selected = section == sec.id, onClick = { section = sec.id },
                            label = { Text(sec.title) }, colors = colorChip(sec.color)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (list.isEmpty()) {
                item { EmptyState(Icons.Default.Gavel, "Тендеров пока нет", "Создайте первый: опишите, что нужно сделать") }
            }
            items(list, key = { it.id }) { t ->
                TenderCard(t) { onOpen(t.id) }
            }
        }
    }
}

@Composable
private fun TenderCard(t: Tender, onClick: () -> Unit) {
    val sec = sectionOf(t.section)
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(6.dp).fillMaxHeight().background(sec.color))
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(sec.icon, contentDescription = null, tint = sec.color, modifier = Modifier.size(18.dp))
                    Text(sec.title, color = sec.color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    if (t.mine) Tag("Мой тендер", Red)
                }
                Spacer(Modifier.height(6.dp))
                Text(t.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(Modifier.height(4.dp))
                Text(t.description, color = Color(0xFF555555), fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (t.budget != null) "Бюджет: ${money(t.budget)}" else "Бюджет: договорной",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${t.place} · ${t.deadline}",
                    color = TextGrey, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${t.offers.size} откликов · ${daysText(t.daysAgo)}",
                    color = TextGrey, fontSize = 12.sp
                )
            }
        }
    }
}

// ---------------------------------------------------------------- Тендер: карточка

@Composable
fun TenderDetailScreen(
    state: AppState, id: Int, onBack: () -> Unit, onBecome: () -> Unit, onToast: (String) -> Unit
) {
    val t = state.tenders.first { it.id == id }
    val sec = sectionOf(t.section)
    var offerDialog by remember { mutableStateOf(false) }
    var needContractor by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(title = {}, navigationIcon = { Back(onBack) }, colors = topColors())
        },
        bottomBar = {
            if (!t.mine) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Button(
                        onClick = { if (state.me == null) needContractor = true else offerDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Red),
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp).height(50.dp)
                    ) { Text("Откликнуться", fontWeight = FontWeight.Bold) }
                }
            }
        }
    ) { inner ->
        Column(Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Tag(sec.title, sec.color)
                if (t.mine) Tag("Мой тендер", Red)
            }
            Spacer(Modifier.height(10.dp))
            Text(t.title, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text(
                if (t.budget != null) money(t.budget) else "Бюджет договорной",
                fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Red
            )
            Spacer(Modifier.height(14.dp))
            InfoRow(Icons.Default.LocationOn, "Место", t.place)
            InfoRow(Icons.Default.Schedule, "Срок", t.deadline)
            Spacer(Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Text("Что нужно сделать", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(t.description, fontSize = 15.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(6.dp))
            Text("${t.author} · ${daysText(t.daysAgo)}", color = TextGrey, fontSize = 12.sp)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Text("Отклики (${t.offers.size})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (t.offers.isEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text("Пока никто не откликнулся", color = TextGrey)
            }
            t.offers.forEach { o ->
                Spacer(Modifier.height(10.dp))
                Surface(color = Grey, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(o.from, fontWeight = FontWeight.Bold)
                        Text(money(o.price), color = Red, fontWeight = FontWeight.Bold)
                        if (o.comment.isNotBlank()) Text(o.comment, fontSize = 14.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (needContractor) {
        AlertDialog(
            onDismissRequest = { needContractor = false },
            title = { Text("Станьте подрядчиком") },
            text = { Text("Чтобы откликаться на тендеры, зарегистрируйтесь как подрядчик: это займёт пару минут.") },
            confirmButton = {
                TextButton(onClick = { needContractor = false; onBecome() }) { Text("Зарегистрироваться", color = Red) }
            },
            dismissButton = { TextButton(onClick = { needContractor = false }) { Text("Позже", color = Red) } },
            containerColor = Color.White
        )
    }

    if (offerDialog) {
        var price by remember { mutableStateOf("") }
        var comment by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { offerDialog = false },
            title = { Text("Ваш отклик") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it.filter(Char::isDigit).take(9) },
                        label = { Text("Ваша цена, ₽") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = fieldColors()
                    )
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it.take(300) },
                        label = { Text("Комментарий") },
                        minLines = 2,
                        colors = fieldColors()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = (price.toIntOrNull() ?: 0) > 0,
                    onClick = {
                        t.offers.add(Offer(state.me?.name ?: "Подрядчик", price.toInt(), comment.trim()))
                        offerDialog = false
                        onToast("Отклик отправлен")
                    }
                ) { Text("Отправить", color = Red) }
            },
            dismissButton = { TextButton(onClick = { offerDialog = false }) { Text("Отмена", color = Red) } },
            containerColor = Color.White
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Red, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, color = TextGrey, modifier = Modifier.width(60.dp))
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

// ---------------------------------------------------------------- Тендер: создание

@Composable
fun CreateTenderScreen(state: AppState, onClose: () -> Unit, onCreated: (Int) -> Unit) {
    var section by remember { mutableStateOf("repair") }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("г. ${state.city}") }
    var negotiable by remember { mutableStateOf(false) }
    var budget by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(deadlines[1]) }

    val budgetValue = budget.toIntOrNull() ?: 0
    val can = title.isNotBlank() && desc.trim().length >= 10 && place.isNotBlank() && (negotiable || budgetValue > 0)
    val recipients = state.contractors.count { it.kind.legal && !it.mine && it.city == state.city }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Новый тендер", fontWeight = FontWeight.Bold) },
                navigationIcon = { Back(onClose) },
                colors = topColors()
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .consumeWindowInsets(inner)
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Раздел", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tenderSections.forEach { sec ->
                    FilterChip(
                        selected = section == sec.id, onClick = { section = sec.id },
                        label = { Text(sec.title) }, colors = colorChip(sec.color),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = title, onValueChange = { title = it.take(90) },
                label = { Text("Что нужно сделать (коротко)") },
                singleLine = true, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = desc, onValueChange = { desc = it.take(2000) },
                label = { Text("Опишите ситуацию") },
                supportingText = { Text("Что есть сейчас, что хотите получить, важные детали, материалы") },
                minLines = 5, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = place, onValueChange = { place = it.take(120) },
                label = { Text("Место (адрес или район)") },
                singleLine = true, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )

            Text("Бюджет", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !negotiable, onClick = { negotiable = false },
                    label = { Text("Указать сумму") }, colors = dealChipColors()
                )
                FilterChip(
                    selected = negotiable, onClick = { negotiable = true },
                    label = { Text("Договорной") }, colors = dealChipColors()
                )
            }
            if (!negotiable) {
                OutlinedTextField(
                    value = budget, onValueChange = { budget = it.filter(Char::isDigit).take(10) },
                    label = { Text("Бюджет, ₽") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors(), modifier = Modifier.fillMaxWidth()
                )
            }

            Text("Сроки", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                deadlines.forEach { d ->
                    FilterChip(
                        selected = deadline == d, onClick = { deadline = d },
                        label = { Text(d) }, colors = dealChipColors(),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Surface(color = RedSoft, shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Red)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Тендер увидят подрядчики, а самозанятым, ИП и ООО в г. ${state.city} придёт уведомление. Сейчас таких: $recipients.",
                        fontSize = 13.sp
                    )
                }
            }

            Button(
                onClick = {
                    val n = state.createTender(
                        Tender(
                            id = state.nextTenderId(),
                            section = section,
                            title = title.trim(),
                            description = desc.trim(),
                            place = place.trim(),
                            city = state.city,
                            budget = if (negotiable) null else budgetValue,
                            deadline = deadline,
                            author = state.userName,
                            daysAgo = 0,
                            mine = true
                        )
                    )
                    onCreated(n)
                },
                enabled = can,
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Опубликовать тендер", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------- Подрядчики: список

@Composable
fun ContractorsScreen(state: AppState, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    var kind by remember { mutableStateOf<ContractorKind?>(null) }
    var service by remember { mutableStateOf<String?>(null) }
    val list = state.cityContractors().filter {
        (kind == null || it.kind == kind) && (service == null || it.services.contains(service))
    }.sortedByDescending { it.rating }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Найти подрядчика", fontWeight = FontWeight.Bold) },
                navigationIcon = { Back(onBack) },
                colors = topColors()
            )
        }
    ) { inner ->
        LazyColumn(Modifier.padding(inner).fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
            item {
                Text(
                    "Мастера и компании в г. ${state.city}",
                    fontSize = 14.sp, color = TextGrey,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                )
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(selected = kind == null, onClick = { kind = null }, label = { Text("Все") }, colors = dealChipColors())
                    }
                    items(ContractorKind.values().toList(), key = { it.name }) { k ->
                        FilterChip(
                            selected = kind == k, onClick = { kind = k },
                            label = { Text(k.plural) }, colors = colorChip(k.color)
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(selected = service == null, onClick = { service = null }, label = { Text("Все услуги") }, colors = dealChipColors())
                    }
                    items(contractorServices, key = { it }) { sv ->
                        FilterChip(selected = service == sv, onClick = { service = sv }, label = { Text(sv) }, colors = dealChipColors())
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (list.isEmpty()) {
                item { EmptyState(Icons.Default.PersonSearch, "Никого не нашли", "Попробуйте другой фильтр или город") }
            }
            items(list, key = { it.id }) { c ->
                ContractorCard(c) { onOpen(c.id) }
            }
        }
    }
}

@Composable
fun ContractorAvatar(c: Contractor, size: Int = 52) {
    Box(
        Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(c.kind.color, c.kind.color.copy(alpha = 0.6f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            c.name.removePrefix("ИП ").first().toString(),
            color = Color.White, fontWeight = FontWeight.Bold, fontSize = (size * 0.42f).sp
        )
    }
}

@Composable
private fun ContractorCard(c: Contractor, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp).fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp)) {
            ContractorAvatar(c)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(c.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    Tag(c.kind.title, c.kind.color)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Text(" ${c.rating} · ${c.jobs} заказов · опыт ${c.experience} л.", fontSize = 13.sp, color = Color(0xFF555555))
                }
                Spacer(Modifier.height(4.dp))
                Text(c.services.joinToString(" · "), fontSize = 13.sp, color = TextGrey, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// ---------------------------------------------------------------- Подрядчик: карточка

@Composable
fun ContractorDetailScreen(state: AppState, id: Int, onBack: () -> Unit, onToast: (String) -> Unit) {
    val c = state.contractors.first { it.id == id }
    Scaffold(
        containerColor = Color.White,
        topBar = { TopAppBar(title = {}, navigationIcon = { Back(onBack) }, colors = topColors()) },
        bottomBar = {
            if (!c.mine) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onToast("Это макет. Телефон: ${c.phone}") },
                            colors = ButtonDefaults.buttonColors(containerColor = Red),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) { Text("Позвонить", fontWeight = FontWeight.Bold) }
                        OutlinedButton(
                            onClick = { onToast("Чат появится позже") },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) { Text("Написать", color = Red, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    ) { inner ->
        Column(Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ContractorAvatar(c, 72)
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(c.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Tag(c.kind.title, c.kind.color)
                        if (c.mine) Tag("Это вы", Red)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300))
                Text(" ${c.rating}  ·  ${c.jobs} заказов  ·  опыт ${c.experience} лет", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Red, modifier = Modifier.size(18.dp))
                Text(" г. ${c.city}", color = TextGrey)
            }
            if (c.inn.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("ИНН ${c.inn}", color = TextGrey, fontSize = 13.sp)
            }
            Spacer(Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            Text("Услуги", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                c.services.forEach { Tag(it, Red) }
            }
            Spacer(Modifier.height(14.dp))
            Text("О себе", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(c.about.ifBlank { "Описание не указано." }, fontSize = 15.sp, lineHeight = 21.sp)
            if (c.kind.legal) {
                Spacer(Modifier.height(14.dp))
                Tag("Получает уведомления о новых тендерах", Color(0xFF00897B))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ---------------------------------------------------------------- Регистрация подрядчика

@Composable
fun BecomeContractorScreen(state: AppState, onClose: () -> Unit, onDone: (Boolean) -> Unit) {
    var isLegal by remember { mutableStateOf(false) }
    var legalKind by remember { mutableStateOf(ContractorKind.SELF) }
    var name by remember { mutableStateOf("") }
    var inn by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf(state.userPhone) }
    var exp by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }
    val services = remember { mutableStateListOf<String>() }

    val kind = if (isLegal) legalKind else ContractorKind.PERSON
    val innLen = if (kind == ContractorKind.OOO) 10 else 12
    val innOk = !isLegal || inn.length == innLen
    val can = name.isNotBlank() && phone.isNotBlank() && services.isNotEmpty() && innOk

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Стать подрядчиком", fontWeight = FontWeight.Bold) },
                navigationIcon = { Back(onClose) },
                colors = topColors()
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .consumeWindowInsets(inner)
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Предлагайте услуги по ремонту, строительству и аренде техники и получайте заказы из тендеров.", color = TextGrey)

            Text("Вы регистрируетесь как", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !isLegal, onClick = { isLegal = false }, label = { Text("Физлицо") }, colors = dealChipColors())
                FilterChip(selected = isLegal, onClick = { isLegal = true }, label = { Text("Юрлицо") }, colors = dealChipColors())
            }

            if (isLegal) {
                Text("Форма", fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(ContractorKind.SELF, ContractorKind.IP, ContractorKind.OOO).forEach { k ->
                        FilterChip(
                            selected = legalKind == k, onClick = { legalKind = k },
                            label = { Text(k.title) }, colors = colorChip(k.color)
                        )
                    }
                }
                Text(
                    when (legalKind) {
                        ContractorKind.SELF -> "Самозанятые: частные мастера."
                        ContractorKind.IP -> "Индивидуальные предприниматели."
                        else -> "Компании (ООО)."
                    } + " Юрлицам приходят уведомления о новых тендерах.",
                    fontSize = 13.sp, color = TextGrey
                )
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it.take(80) },
                label = { Text(if (kind == ContractorKind.OOO) "Название компании" else "ФИО") },
                singleLine = true, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )
            if (isLegal) {
                OutlinedTextField(
                    value = inn, onValueChange = { inn = it.filter(Char::isDigit).take(innLen) },
                    label = { Text("ИНН ($innLen цифр)") },
                    singleLine = true,
                    isError = inn.isNotEmpty() && inn.length != innLen,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = fieldColors(), modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = phone, onValueChange = { phone = it.take(20) },
                label = { Text("Телефон") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = exp, onValueChange = { exp = it.filter(Char::isDigit).take(2) },
                label = { Text("Опыт, лет") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )

            Text("Какие услуги предлагаете", fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                contractorServices.forEach { sv ->
                    FilterChip(
                        selected = services.contains(sv),
                        onClick = { if (!services.remove(sv)) services.add(sv) },
                        label = { Text(sv) }, colors = dealChipColors(),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = about, onValueChange = { about = it.take(1000) },
                label = { Text("О себе и примеры работ") },
                minLines = 3, colors = fieldColors(), modifier = Modifier.fillMaxWidth()
            )
            Text("Город: ${state.city}", color = TextGrey)

            Button(
                onClick = {
                    state.registerContractor(
                        Contractor(
                            id = state.nextContractorId(),
                            name = name.trim(),
                            kind = kind,
                            city = state.city,
                            services = services.toList(),
                            experience = exp.toIntOrNull() ?: 0,
                            about = about.trim(),
                            phone = phone.trim(),
                            rating = 5.0f,
                            jobs = 0,
                            inn = if (isLegal) inn else "",
                            mine = true
                        )
                    )
                    onDone(kind.legal)
                },
                enabled = can,
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Зарегистрироваться", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ---------------------------------------------------------------- Уведомления

@Composable
fun NotificationsScreen(state: AppState, onBack: () -> Unit, onTender: (Int) -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Уведомления", fontWeight = FontWeight.Bold) },
                navigationIcon = { Back(onBack) },
                colors = topColors()
            )
        }
    ) { inner ->
        LazyColumn(Modifier.padding(inner).fillMaxSize()) {
            if (state.notifications.isEmpty()) {
                item {
                    EmptyState(
                        Icons.Default.Notifications, "Пока нет уведомлений",
                        "Зарегистрируйтесь как самозанятый, ИП или ООО, и вам будут приходить новые тендеры вашего города"
                    )
                }
            }
            items(state.notifications) { n ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            n.read = true
                            n.tenderId?.let(onTender)
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape).background(RedSoft),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Gavel, contentDescription = null, tint = Red) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(n.title, fontWeight = if (n.read) FontWeight.Normal else FontWeight.Bold)
                        Text(n.text, color = TextGrey, fontSize = 14.sp)
                    }
                    if (!n.read) {
                        Box(Modifier.padding(top = 6.dp).size(10.dp).clip(CircleShape).background(Red))
                    }
                }
                HorizontalDivider()
            }
        }
    }
}
