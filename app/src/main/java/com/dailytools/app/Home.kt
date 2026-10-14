@file:OptIn(ExperimentalMaterial3Api::class)

package com.dailytools.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun dealChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Red,
    selectedLabelColor = Color.White
)

// ---------------------------------------------------------------- Главная

@Composable
fun HomeScreen(
    state: AppState,
    onAd: (Int) -> Unit,
    onCategory: (String) -> Unit,
    onAddAd: () -> Unit
) {
    var deal by remember { mutableStateOf<DealType?>(null) }
    val ads = state.cityAds().filter { deal == null || it.type == deal }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { Banner(onAddAd) }

        item(span = { GridItemSpan(maxLineSpan) }) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.id }) { cat ->
                    Column(
                        Modifier.width(80.dp).clickable { onCategory(cat.id) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ToolArt(
                            cat.id,
                            categories.indexOfFirst { it.id == cat.id },
                            Modifier.size(64.dp).clip(RoundedCornerShape(18.dp))
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            cat.title,
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = deal == null, onClick = { deal = null },
                    label = { Text("Все") }, colors = dealChipColors()
                )
                FilterChip(
                    selected = deal == DealType.SALE, onClick = { deal = DealType.SALE },
                    label = { Text("Купить") }, colors = dealChipColors()
                )
                FilterChip(
                    selected = deal == DealType.RENT, onClick = { deal = DealType.RENT },
                    label = { Text("Арендовать") }, colors = dealChipColors()
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                "Рекомендации в г. ${state.city}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (ads.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(Icons.Default.SearchOff, "Пока пусто", "В этом городе ещё нет таких объявлений")
            }
        }

        items(ads, key = { it.id }) { ad ->
            AdCard(ad, state.isFav(ad.id), { state.toggleFav(ad.id) }, { onAd(ad.id) })
        }
    }
}

@Composable
private fun Banner(onAddAd: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(Red, RedDark)))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Продайте или сдайте\nинструмент в аренду",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text("Бесплатно и за пару минут", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onAddAd,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Red)
            ) { Text("Подать объявление", fontWeight = FontWeight.Bold) }
        }
    }
}

// ---------------------------------------------------------------- Каталог

@Composable
fun CatalogScreen(state: AppState, onCategory: (String) -> Unit) {
    val ads = state.cityAds()
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text(
                "Каталог",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        items(categories, key = { it.id }) { cat ->
            val count = ads.count { it.categoryId == cat.id }
            Row(
                Modifier.fillMaxWidth().clickable { onCategory(cat.id) }.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolArt(
                    cat.id,
                    categories.indexOfFirst { it.id == cat.id },
                    Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(cat.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text("$count объявл. в г. ${state.city}", color = TextGrey, fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGrey)
            }
            HorizontalDivider(Modifier.padding(start = 82.dp))
        }
    }
}

// ---------------------------------------------------------------- Поиск

@Composable
fun SearchResultsScreen(state: AppState, onAd: (Int) -> Unit) {
    val found = state.search()
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text(
                "Найдено: ${found.size} в г. ${state.city}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        if (found.isEmpty()) {
            item {
                EmptyState(
                    Icons.Default.SearchOff,
                    "Ничего не найдено",
                    "Попробуйте изменить запрос или выбрать другой город"
                )
            }
        }
        items(found, key = { it.id }) { ad ->
            AdRow(ad, state.isFav(ad.id), { state.toggleFav(ad.id) }, { onAd(ad.id) })
            HorizontalDivider()
        }
    }
}

// ---------------------------------------------------------------- Избранное

@Composable
fun FavoritesScreen(state: AppState, onAd: (Int) -> Unit) {
    val favs = state.ads.filter { state.isFav(it.id) }
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Text(
                "Избранное",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        if (favs.isEmpty()) {
            item {
                EmptyState(
                    Icons.Default.FavoriteBorder,
                    "Здесь будут ваши избранные",
                    "Нажмите на сердечко у объявления, чтобы сохранить его"
                )
            }
        }
        items(favs, key = { it.id }) { ad ->
            AdRow(ad, true, { state.toggleFav(ad.id) }, { onAd(ad.id) })
            HorizontalDivider()
        }
    }
}
