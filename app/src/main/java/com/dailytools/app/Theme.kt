package com.dailytools.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Red = Color(0xFFE31E24)
val RedDark = Color(0xFFB71419)
val RedSoft = Color(0xFFFDE8E9)
val Grey = Color(0xFFF2F2F2)
val TextGrey = Color(0xFF8A8A8A)

private val Scheme = lightColorScheme(
    primary = Red,
    onPrimary = Color.White,
    primaryContainer = RedSoft,
    onPrimaryContainer = RedDark,
    secondary = Red,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Grey,
    onSurfaceVariant = Color(0xFF444444),
    surfaceTint = Color.White,
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE3E3E3)
)

@Composable
fun DailyTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
