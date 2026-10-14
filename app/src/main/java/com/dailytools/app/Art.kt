package com.dailytools.app

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

/** Цветные фоны для картинок: основной цвет и более светлый. */
val artPalettes = listOf(
    Color(0xFFE31E24) to Color(0xFFFF8A65), // красный
    Color(0xFFFF8F00) to Color(0xFFFFCA28), // янтарный
    Color(0xFFD81B60) to Color(0xFFFF80AB), // малиновый
    Color(0xFF00897B) to Color(0xFF4DB6AC), // бирюзовый
    Color(0xFF3949AB) to Color(0xFF7986CB), // индиго
    Color(0xFF43A047) to Color(0xFF9CCC65)  // зелёный
)

/**
 * Нарисованная «фотография» инструмента или техники на цветном фоне.
 * Пока это иллюстрации; настоящие фото появятся, когда пользователи начнут их загружать.
 */
@Composable
fun ToolArt(categoryId: String, variant: Int, modifier: Modifier = Modifier) {
    val pal = artPalettes[Math.floorMod(variant, artPalettes.size)]
    Canvas(modifier) {
        drawRect(
            Brush.linearGradient(
                listOf(pal.first, pal.second),
                start = Offset.Zero,
                end = Offset(size.width, size.height)
            )
        )
        val m = minOf(size.width, size.height)
        drawCircle(
            Color.White.copy(alpha = 0.14f), m * 0.6f,
            Offset(size.width * (0.1f + 0.15f * (variant % 4)), size.height * 0.1f)
        )
        drawCircle(
            Color.White.copy(alpha = 0.10f), m * 0.45f,
            Offset(size.width * 0.95f, size.height * (0.7f + 0.05f * (variant % 3)))
        )
        val tilt = ((variant % 5) - 2) * 3f
        rotate(tilt, pivot = center) { drawToolGlyph(categoryId, pal.first) }
    }
}

private fun DrawScope.drawToolGlyph(cat: String, tint: Color) {
    val s = minOf(size.width, size.height) * 0.74f / 100f
    val ox = (size.width - 100f * s) / 2f
    val oy = (size.height - 100f * s) / 2f
    val w = Color.White
    val shade = Color.Black.copy(alpha = 0.22f)
    val yellow = Color(0xFFFFEE58)

    fun p(x: Float, y: Float) = Offset(ox + x * s, oy + y * s)
    fun sz(a: Float, b: Float) = Size(a * s, b * s)
    fun rr(c: Color, x1: Float, y1: Float, x2: Float, y2: Float, r: Float = 4f) =
        drawRoundRect(c, p(x1, y1), sz(x2 - x1, y2 - y1), CornerRadius(r * s))
    fun ln(c: Color, x1: Float, y1: Float, x2: Float, y2: Float, wd: Float) =
        drawLine(c, p(x1, y1), p(x2, y2), strokeWidth = wd * s, cap = StrokeCap.Round)
    fun ci(c: Color, x: Float, y: Float, r: Float) = drawCircle(c, r * s, p(x, y))
    fun poly(c: Color, vararg v: Float) {
        val path = Path()
        path.moveTo(ox + v[0] * s, oy + v[1] * s)
        var i = 2
        while (i < v.size) {
            path.lineTo(ox + v[i] * s, oy + v[i + 1] * s)
            i += 2
        }
        path.close()
        drawPath(path, c)
    }

    when (cat) {
        "power" -> { // дрель
            rr(w, 16f, 34f, 70f, 56f, 8f)
            poly(w, 30f, 54f, 48f, 54f, 44f, 86f, 28f, 86f)
            rr(shade, 28f, 80f, 48f, 90f, 3f)
            rr(w, 68f, 38f, 82f, 52f, 3f)
            rr(w, 82f, 43f, 96f, 47f, 2f)
            rr(shade, 22f, 40f, 40f, 46f, 3f)
            ci(tint, 60f, 45f, 4f)
        }
        "hand" -> { // молоток
            rotate(-35f, pivot = p(50f, 50f)) {
                rr(w, 46f, 26f, 54f, 92f, 3f)
                rr(w, 26f, 14f, 74f, 34f, 5f)
                rr(shade, 26f, 14f, 38f, 34f, 5f)
            }
        }
        "heavy" -> { // экскаватор
            rr(shade, 10f, 76f, 72f, 90f, 7f)
            ci(w, 20f, 83f, 4f); ci(w, 41f, 83f, 4f); ci(w, 62f, 83f, 4f)
            rr(w, 16f, 54f, 56f, 76f, 5f)
            rr(shade, 22f, 58f, 38f, 70f, 3f)
            ln(w, 52f, 60f, 74f, 30f, 8f)
            ln(w, 74f, 30f, 92f, 52f, 7f)
            poly(w, 90f, 50f, 98f, 66f, 84f, 68f)
        }
        "lift" -> { // строительные леса
            ln(w, 22f, 18f, 22f, 88f, 4f); ln(w, 50f, 18f, 50f, 88f, 4f); ln(w, 78f, 18f, 78f, 88f, 4f)
            ln(w, 22f, 30f, 78f, 30f, 4f); ln(w, 22f, 52f, 78f, 52f, 4f); ln(w, 22f, 74f, 78f, 74f, 4f)
            ln(w, 22f, 30f, 50f, 52f, 3f); ln(w, 50f, 52f, 78f, 74f, 3f)
            rr(w, 16f, 52f, 84f, 58f, 2f); rr(w, 16f, 74f, 84f, 80f, 2f)
        }
        "weld" -> { // сварочная маска
            poly(w, 26f, 28f, 74f, 28f, 70f, 72f, 50f, 82f, 30f, 72f)
            rr(shade, 34f, 40f, 66f, 54f, 3f)
            ln(yellow, 80f, 70f, 96f, 60f, 3f)
            ln(yellow, 82f, 78f, 98f, 82f, 3f)
            ln(yellow, 78f, 62f, 86f, 48f, 3f)
        }
        "measure" -> { // уровень
            rotate(-18f, pivot = p(50f, 50f)) {
                rr(w, 6f, 40f, 94f, 60f, 5f)
                rr(shade, 40f, 46f, 60f, 54f, 4f)
                ci(yellow, 50f, 50f, 3f)
                for (i in 0..4) {
                    ln(shade, 14f + i * 5f, 42f, 14f + i * 5f, 47f, 1.6f)
                    ln(shade, 66f + i * 5f, 42f, 66f + i * 5f, 47f, 1.6f)
                }
            }
        }
        "paint" -> { // валик
            rr(w, 18f, 20f, 74f, 42f, 7f)
            rr(shade, 18f, 20f, 26f, 42f, 4f)
            ln(w, 74f, 31f, 86f, 31f, 4f)
            ln(w, 86f, 31f, 86f, 54f, 4f)
            ln(w, 86f, 54f, 52f, 54f, 4f)
            ln(w, 52f, 54f, 52f, 62f, 4f)
            rr(w, 45f, 62f, 59f, 90f, 4f)
        }
        "plumb" -> { // труба с вентилем
            ln(w, 14f, 72f, 50f, 72f, 13f)
            ln(w, 50f, 72f, 50f, 38f, 13f)
            ln(w, 50f, 38f, 86f, 38f, 13f)
            rr(shade, 18f, 64f, 24f, 80f, 2f)
            rr(shade, 76f, 30f, 82f, 46f, 2f)
            ci(shade, 50f, 56f, 9f)
            ci(w, 50f, 56f, 4f)
        }
        "gen" -> { // генератор
            rr(w, 12f, 38f, 88f, 82f, 8f)
            rr(shade, 20f, 46f, 44f, 74f, 4f)
            poly(yellow, 66f, 44f, 54f, 64f, 63f, 64f, 58f, 80f, 76f, 58f, 66f, 58f, 72f, 44f)
            ln(w, 24f, 38f, 24f, 28f, 4f); ln(w, 76f, 38f, 76f, 28f, 4f); ln(w, 24f, 28f, 76f, 28f, 4f)
            rr(shade, 18f, 82f, 30f, 88f, 2f); rr(shade, 70f, 82f, 82f, 88f, 2f)
        }
        "truck" -> { // грузовик
            rr(w, 8f, 38f, 56f, 70f, 4f)
            poly(w, 58f, 48f, 76f, 48f, 92f, 62f, 92f, 70f, 58f, 70f)
            poly(shade, 64f, 53f, 75f, 53f, 85f, 62f, 64f, 62f)
            ci(shade, 26f, 74f, 10f); ci(w, 26f, 74f, 5f)
            ci(shade, 74f, 74f, 10f); ci(w, 74f, 74f, 5f)
        }
        "wood" -> { // пильный диск
            for (i in 0 until 14) {
                rotate(i * 360f / 14f, pivot = p(50f, 50f)) { poly(w, 46f, 14f, 54f, 14f, 50f, 2f) }
            }
            ci(w, 50f, 50f, 38f)
            drawCircle(shade, 28f * s, p(50f, 50f), style = Stroke(2f * s))
            ci(shade, 50f, 50f, 12f)
            ci(w, 50f, 50f, 5f)
        }
        "mat" -> { // гайка и саморез
            val hex = FloatArray(12)
            for (k in 0 until 6) {
                val a = Math.toRadians(60.0 * k).toFloat()
                hex[k * 2] = 34f + 22f * cos(a)
                hex[k * 2 + 1] = 38f + 22f * sin(a)
            }
            poly(w, *hex)
            ci(shade, 34f, 38f, 9f)
            rotate(35f, pivot = p(66f, 66f)) {
                rr(w, 54f, 40f, 78f, 50f, 4f)
                rr(w, 62f, 48f, 70f, 92f, 2f)
                ln(shade, 62f, 60f, 70f, 63f, 2f)
                ln(shade, 62f, 68f, 70f, 71f, 2f)
                ln(shade, 62f, 76f, 70f, 79f, 2f)
            }
        }
        else -> ci(w, 50f, 50f, 30f)
    }
}
