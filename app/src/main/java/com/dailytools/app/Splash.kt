package com.dailytools.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/** Заставка: белый фон, красное название плавно появляется, «грузится» и плавно гаснет. */
@Composable
fun SplashScreen(onFinish: () -> Unit) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        delay(1400) // «загрузка»
        alpha.animateTo(0f, tween(800, easing = FastOutSlowInEasing))
        onFinish()
    }

    Box(
        Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "DailyTools",
            color = Red,
            fontSize = 44.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.alpha(alpha.value)
        )
    }
}
