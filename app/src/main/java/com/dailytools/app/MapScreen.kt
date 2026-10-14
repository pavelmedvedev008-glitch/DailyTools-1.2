package com.dailytools.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/** Условная карта, нарисованная кодом. Настоящая карта (с геолокацией) будет добавлена позже. */
@Composable
fun MapScreen(state: AppState, onAd: (Int) -> Unit) {
    var selected by remember { mutableStateOf<Int?>(null) }
    val ads = state.cityAds()
    val sel = ads.firstOrNull { it.id == selected }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = constraints.maxWidth
        val h = constraints.maxHeight

        Canvas(Modifier.fillMaxSize().clickable { selected = null }) {
            val cw = size.width
            val ch = size.height
            drawRect(Color(0xFFF1EFEA))

            // море
            val sea = Path().apply {
                moveTo(0f, 0f)
                lineTo(cw * 0.20f, 0f)
                cubicTo(cw * 0.12f, ch * 0.30f, cw * 0.28f, ch * 0.60f, cw * 0.16f, ch)
                lineTo(0f, ch)
                close()
            }
            drawPath(sea, Color(0xFFD6E8F5))

            // парк
            drawRoundRect(
                Color(0xFFDDEBD3), Offset(cw * 0.55f, ch * 0.08f), Size(cw * 0.32f, ch * 0.16f),
                CornerRadius(28f)
            )
            drawRoundRect(
                Color(0xFFDDEBD3), Offset(cw * 0.30f, ch * 0.70f), Size(cw * 0.22f, ch * 0.14f),
                CornerRadius(28f)
            )

            // дороги
            fun road(x1: Float, y1: Float, x2: Float, y2: Float, width: Float) =
                drawLine(Color.White, Offset(x1 * cw, y1 * ch), Offset(x2 * cw, y2 * ch), strokeWidth = width)
            road(0.25f, 0f, 0.30f, 1f, 26f)
            road(0.50f, 0f, 0.52f, 1f, 18f)
            road(0.78f, 0f, 0.74f, 1f, 18f)
            road(0.18f, 0.28f, 1f, 0.26f, 22f)
            road(0.18f, 0.55f, 1f, 0.60f, 26f)
            road(0.20f, 0.85f, 1f, 0.82f, 18f)
            road(0.62f, 0.26f, 0.64f, 0.60f, 12f)
        }

        ads.forEach { ad ->
            val isSel = ad.id == selected
            Surface(
                color = if (isSel) RedDark else Red,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .offset { IntOffset((ad.mapX * w).roundToInt(), (ad.mapY * h).roundToInt()) }
                    .zIndex(if (isSel) 1f else 0f)
                    .clickable { selected = ad.id }
            ) {
                Text(
                    shortPrice(ad),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 2.dp,
            modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
        ) {
            Text(
                "Условная карта · настоящая будет позже",
                fontSize = 12.sp,
                color = TextGrey,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        if (sel != null) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
            ) {
                AdRow(sel, state.isFav(sel.id), { state.toggleFav(sel.id) }, { onAd(sel.id) })
            }
        }
    }
}
