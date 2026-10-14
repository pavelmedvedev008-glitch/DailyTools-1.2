@file:OptIn(ExperimentalMaterial3Api::class)

package com.dailytools.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
private fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
    }
}

// ---------------------------------------------------------------- Карточка объявления

@Composable
fun DetailScreen(state: AppState, id: Int, onBack: () -> Unit, onToast: (String) -> Unit) {
    val ad = state.ads.first { it.id == id }
    val fav = state.isFav(id)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { BackButton(onBack) },
                actions = {
                    IconButton(onClick = { onToast("Поделиться появится позже") }) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                    IconButton(onClick = { state.toggleFav(id) }) {
                        Icon(
                            if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "В избранное",
                            tint = if (fav) Red else Color(0xFF333333)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Row(
                    Modifier.fillMaxWidth().navigationBarsPadding().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onToast("Это макет. Телефон продавца: ${ad.phone}") },
                        colors = ButtonDefaults.buttonColors(containerColor = Red),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("Позвонить", fontWeight = FontWeight.Bold) }
                    OutlinedButton(
                        onClick = { onToast("Чат с продавцом появится позже") },
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) { Text("Написать", color = Red, fontWeight = FontWeight.Bold) }
                }
            }
        }
    ) { inner ->
        Column(Modifier.padding(inner).fillMaxSize().verticalScroll(rememberScrollState())) {
            val pager = rememberPagerState(pageCount = { 3 })
            Box {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth().height(260.dp)) { page ->
                    AdPlaceholder(ad, Modifier.fillMaxSize(), slide = page * 2)
                }
                Row(
                    Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { i ->
                        Box(
                            Modifier
                                .size(if (pager.currentPage == i) 9.dp else 7.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = if (pager.currentPage == i) 1f else 0.55f))
                        )
                    }
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
                ) {
                    Text(
                        "${pager.currentPage + 1}/3",
                        color = Color.White, fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Column(Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    DealBadge(ad.type)
                    Text(ad.condition, color = TextGrey, fontSize = 13.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(formatPrice(ad), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(4.dp))
                Text(ad.title, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("${ad.city} · ${daysText(ad.daysAgo)}", color = TextGrey)

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Text("Описание", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(ad.description, fontSize = 15.sp, lineHeight = 21.sp)

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Text("Характеристики", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Spec("Категория", categoryOf(ad.categoryId).title.replace("-", ""))
                Spec("Тип", if (ad.type == DealType.RENT) "Аренда" else "Продажа")
                Spec("Состояние", ad.condition)
                Spec("Город", ad.city)

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(52.dp).clip(CircleShape).background(RedSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ad.seller.first().toString(), color = Red, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(ad.seller, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Частное лицо · на DailyTools с 2025 года", color = TextGrey, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun Spec(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = TextGrey, modifier = Modifier.width(130.dp))
        Text(value, modifier = Modifier.weight(1f))
    }
}

// ---------------------------------------------------------------- Список объявлений (категория, мои)

@Composable
fun AdListScreen(
    title: String,
    ads: List<Ad>,
    state: AppState,
    emptyText: String,
    onBack: () -> Unit,
    onAd: (Int) -> Unit
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = { BackButton(onBack) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { inner ->
        LazyColumn(Modifier.padding(inner).fillMaxSize()) {
            if (ads.isEmpty()) {
                item { EmptyState(Icons.Default.Inventory2, "Пока пусто", emptyText) }
            }
            items(ads, key = { it.id }) { ad ->
                AdRow(ad, state.isFav(ad.id), { state.toggleFav(ad.id) }, { onAd(ad.id) })
                HorizontalDivider()
            }
        }
    }
}

// ---------------------------------------------------------------- Новое объявление

@Composable
fun AddAdScreen(state: AppState, onClose: () -> Unit, onPublished: () -> Unit) {
    var type by remember { mutableStateOf(DealType.SALE) }
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf(categories.first().id) }
    var cond by remember { mutableStateOf("Б/у") }

    val priceValue = price.toIntOrNull() ?: 0
    val canPublish = title.isNotBlank() && priceValue > 0

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Новое объявление", fontWeight = FontWeight.Bold) },
                navigationIcon = { BackButton(onClose) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            Text("Что вы хотите сделать?", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == DealType.SALE, onClick = { type = DealType.SALE },
                    label = { Text("Продать") }, colors = dealChipColors()
                )
                FilterChip(
                    selected = type == DealType.RENT, onClick = { type = DealType.RENT },
                    label = { Text("Сдать в аренду") }, colors = dealChipColors()
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(RedSoft),
                contentAlignment = Alignment.Center
            ) {
                Text("Добавить фото (появится позже)", color = Red)
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it.take(80) },
                label = { Text("Название") },
                singleLine = true,
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Категория", fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.id }) { c ->
                    FilterChip(
                        selected = cat == c.id, onClick = { cat = c.id },
                        label = { Text(c.title.replace("-", "")) }, colors = dealChipColors()
                    )
                }
            }

            OutlinedTextField(
                value = price,
                onValueChange = { price = it.filter(Char::isDigit).take(9) },
                label = { Text(if (type == DealType.RENT) "Цена за сутки, ₽" else "Цена, ₽") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Состояние", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Новое", "Б/у", "Хорошее").forEach { c ->
                    FilterChip(
                        selected = cond == c, onClick = { cond = c },
                        label = { Text(c) }, colors = dealChipColors()
                    )
                }
            }

            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it.take(1000) },
                label = { Text("Описание") },
                minLines = 4,
                colors = fieldColors(),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Город: ${state.city}", color = TextGrey)

            Button(
                onClick = {
                    state.addAd(
                        Ad(
                            id = state.nextId(),
                            title = title.trim(),
                            price = priceValue,
                            type = type,
                            categoryId = cat,
                            city = state.city,
                            description = desc.trim().ifEmpty { "Описание не указано." },
                            seller = state.userName,
                            phone = state.userPhone,
                            daysAgo = 0,
                            condition = cond,
                            mapX = 0.22f + Random.nextFloat() * 0.6f,
                            mapY = 0.08f + Random.nextFloat() * 0.7f,
                            mine = true
                        )
                    )
                    onPublished()
                },
                enabled = canPublish,
                colors = ButtonDefaults.buttonColors(containerColor = Red),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Опубликовать", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(16.dp))
        }
    }
}
