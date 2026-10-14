@file:OptIn(ExperimentalMaterial3Api::class)

package com.dailytools.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

// ---------------------------------------------------------------- форматирование

fun formatPrice(ad: Ad): String {
    val num = "%,d".format(Locale.US, ad.price).replace(',', ' ')
    return if (ad.type == DealType.RENT) "$num ₽/сутки" else "$num ₽"
}

fun shortPrice(ad: Ad): String {
    val p = ad.price
    val text = when {
        p >= 1_000_000 -> trimZero(p / 1_000_000.0) + " млн"
        p >= 1_000 -> trimZero(p / 1_000.0) + " тыс"
        else -> p.toString()
    }
    return text.replace('.', ',') + " ₽"
}

private fun trimZero(v: Double): String {
    val r = Math.round(v * 10) / 10.0
    return if (r % 1.0 == 0.0) r.toInt().toString() else r.toString()
}

fun daysText(d: Int): String = when (d) {
    0 -> "Сегодня"
    1 -> "Вчера"
    else -> "$d дн. назад"
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Red,
    focusedLabelColor = Red,
    cursorColor = Red
)

// ---------------------------------------------------------------- верхние панели

@Composable
fun SearchTopBar(state: AppState, onCityClick: () -> Unit) {
    val focus = LocalFocusManager.current
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
            .padding(top = 8.dp, bottom = 2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Grey)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Red)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = state.query,
                onValueChange = { state.query = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                cursorBrush = SolidColor(Red),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (state.query.isEmpty()) {
                            Text(
                                "Поиск по объявлениям в г. ${state.city}",
                                color = TextGrey,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        inner()
                    }
                }
            )
            if (state.query.isNotEmpty()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Очистить",
                    tint = TextGrey,
                    modifier = Modifier.clip(CircleShape).clickable { state.query = "" }
                )
            }
        }
        Row(
            Modifier.clickable(onClick = onCityClick).padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Red, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(2.dp))
            Text(state.city, color = Red, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Red, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun TitleBar(title: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
    }
}

// ---------------------------------------------------------------- элементы объявления

@Composable
fun AdPlaceholder(ad: Ad, modifier: Modifier, slide: Int = 0) {
    ToolArt(ad.categoryId, ad.id + slide, modifier)
}

@Composable
fun DealBadge(type: DealType, modifier: Modifier = Modifier) {
    val rent = type == DealType.RENT
    Surface(
        color = if (rent) Red else RedSoft,
        contentColor = if (rent) Color.White else Red,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            if (rent) "Аренда" else "Продажа",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun FavButton(fav: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(6.dp)
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.92f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "В избранное",
            tint = if (fav) Red else Color(0xFF555555),
            modifier = Modifier.size(20.dp)
        )
    }
}

/** Карточка для сетки (как на главной Авито). */
@Composable
fun AdCard(ad: Ad, fav: Boolean, onFav: () -> Unit, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.clickable(onClick = onClick)) {
        Box {
            AdPlaceholder(
                ad,
                Modifier.fillMaxWidth().aspectRatio(1.25f).clip(RoundedCornerShape(12.dp)),
            )
            FavButton(fav, onFav, Modifier.align(Alignment.TopEnd))
            DealBadge(ad.type, Modifier.align(Alignment.BottomStart).padding(8.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(formatPrice(ad), fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(ad.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
        Text("${ad.city} · ${daysText(ad.daysAgo)}", color = TextGrey, fontSize = 12.sp)
    }
}

/** Строка для списков (поиск, избранное, мои объявления). */
@Composable
fun AdRow(ad: Ad, fav: Boolean, onFav: () -> Unit, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        AdPlaceholder(ad, Modifier.size(width = 120.dp, height = 96.dp).clip(RoundedCornerShape(12.dp)))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(formatPrice(ad), fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(ad.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
            Spacer(Modifier.height(2.dp))
            Text("${ad.city} · ${daysText(ad.daysAgo)}", color = TextGrey, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            DealBadge(ad.type)
        }
        Icon(
            if (fav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "В избранное",
            tint = if (fav) Red else Color(0xFF777777),
            modifier = Modifier
                .padding(start = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onFav)
                .padding(3.dp)
        )
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, subtitle: String) {
    Column(
        Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(72.dp).clip(CircleShape).background(RedSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Red, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = TextGrey, textAlign = TextAlign.Center)
    }
}
