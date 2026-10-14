@file:OptIn(ExperimentalMaterial3Api::class)

package com.dailytools.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    state: AppState,
    onMyAds: () -> Unit,
    onAddAd: () -> Unit,
    onFavorites: () -> Unit,
    onTenders: () -> Unit,
    onContractors: () -> Unit,
    onBecome: () -> Unit,
    onNotifications: () -> Unit,
    onToast: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (state.loggedIn) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(64.dp).clip(CircleShape).background(Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        state.userName.first().toString(),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(state.userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(state.userPhone, color = TextGrey)
                }
            }
        } else {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(80.dp).clip(CircleShape).background(RedSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Red, modifier = Modifier.size(40.dp))
                }
                Spacer(Modifier.height(12.dp))
                Text("Войдите в DailyTools", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Чтобы подавать объявления и общаться с продавцами",
                    color = TextGrey,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        state.loggedIn = true
                        onToast("Это макет: настоящий вход по номеру телефона появится позже")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Red),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Войти") }
            }
        }

        HorizontalDivider()

        Text(
            "Подрядчики и тендеры",
            fontSize = 18.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 10.dp)
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FeatureTile("Тендеры", Icons.Default.Gavel, listOf(Color(0xFFFF8F00), Color(0xFFFFCA28)), Modifier.weight(1f), onTenders)
            FeatureTile("Найти подрядчика", Icons.Default.PersonSearch, listOf(Color(0xFF3949AB), Color(0xFF7986CB)), Modifier.weight(1f), onContractors)
            FeatureTile(
                if (state.me == null) "Стать подрядчиком" else "Мой профиль подрядчика",
                Icons.Default.HowToReg, listOf(Color(0xFF00897B), Color(0xFF4DB6AC)), Modifier.weight(1f), onBecome
            )
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        MenuRow(Icons.Default.Notifications, "Уведомления" + if (state.unread > 0) " (${state.unread})" else "", onNotifications)
        MenuRow(Icons.Default.Inventory2, "Мои объявления", onMyAds)
        MenuRow(Icons.Default.AddCircleOutline, "Подать объявление", onAddAd)
        MenuRow(Icons.Default.FavoriteBorder, "Избранное", onFavorites)
        MenuRow(Icons.Default.Settings, "Настройки") { onToast("Настройки появятся позже") }
        MenuRow(Icons.Default.HelpOutline, "Помощь") { onToast("Раздел помощи появится позже") }
        if (state.loggedIn) {
            MenuRow(Icons.Default.Logout, "Выйти") { state.loggedIn = false }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "DailyTools · версия макета 0.1",
            color = TextGrey,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FeatureTile(title: String, icon: ImageVector, colors: List<Color>, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .height(104.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(colors))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp).align(Alignment.TopStart))
        Text(
            title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp,
            lineHeight = 16.sp, modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun MenuRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Red)
        Spacer(Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGrey)
    }
    HorizontalDivider(Modifier.padding(start = 56.dp))
}
