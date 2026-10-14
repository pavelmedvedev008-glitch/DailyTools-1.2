@file:OptIn(ExperimentalMaterial3Api::class)

package com.dailytools.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        setContent {
            DailyTheme {
                Surface(Modifier.fillMaxSize(), color = Color.White) { Root() }
            }
        }
    }
}

@Composable
fun Root() {
    var splash by remember { mutableStateOf(true) }
    Crossfade(targetState = splash, animationSpec = tween(600), label = "root") { showSplash ->
        if (showSplash) SplashScreen { splash = false } else DailyApp()
    }
}

sealed interface Route {
    data class Detail(val id: Int) : Route
    data class Category(val id: String) : Route
    data object MyAds : Route
    data object AddAd : Route
    data object Tenders : Route
    data class TenderDetail(val id: Int) : Route
    data object CreateTender : Route
    data object Contractors : Route
    data class ContractorDetail(val id: Int) : Route
    data object BecomeContractor : Route
    data object Notifications : Route
}

private const val TAB_HOME = 0
private const val TAB_CATALOG = 1
private const val TAB_PROFILE = 2
private const val TAB_FAV = 3
private const val TAB_MAP = 4

@Composable
fun DailyApp() {
    val state = remember { AppState() }
    var tab by remember { mutableIntStateOf(TAB_HOME) }
    val stack = remember { mutableStateListOf<Route>() }
    var cityDialog by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val toast: (String) -> Unit = { msg ->
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(msg)
        }
    }
    val openAd: (Int) -> Unit = { stack.add(Route.Detail(it)) }
    val openCategory: (String) -> Unit = { stack.add(Route.Category(it)) }
    val pop: () -> Unit = { if (stack.isNotEmpty()) stack.removeAt(stack.lastIndex) }

    BackHandler(enabled = stack.isNotEmpty()) { pop() }
    BackHandler(enabled = stack.isEmpty() && state.query.isNotEmpty()) { state.query = "" }
    BackHandler(enabled = stack.isEmpty() && state.query.isEmpty() && tab != TAB_HOME) { tab = TAB_HOME }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                if (tab == TAB_PROFILE) TitleBar("Профиль")
                else SearchTopBar(state) { cityDialog = true }
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                    val items = listOf(
                        Triple(TAB_HOME, "Главная", Icons.Default.Home),
                        Triple(TAB_CATALOG, "Каталог", Icons.Default.GridView),
                        Triple(TAB_PROFILE, "Профиль", Icons.Default.Person),
                        Triple(TAB_FAV, "Избранное", if (state.favorites.isEmpty()) Icons.Default.FavoriteBorder else Icons.Default.Favorite),
                        Triple(TAB_MAP, "Карты", Icons.Default.Map)
                    )
                    items.forEach { (index, label, icon) ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 11.sp, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Red,
                                selectedTextColor = Red,
                                indicatorColor = RedSoft,
                                unselectedIconColor = Color(0xFF6B6B6B),
                                unselectedTextColor = Color(0xFF6B6B6B)
                            )
                        )
                    }
                }
            }
        ) { inner ->
            Box(Modifier.padding(inner)) {
                if (state.query.isNotBlank() && tab != TAB_PROFILE) {
                    SearchResultsScreen(state, openAd)
                } else {
                    when (tab) {
                        TAB_HOME -> HomeScreen(state, openAd, openCategory) { stack.add(Route.AddAd) }
                        TAB_CATALOG -> CatalogScreen(state, openCategory)
                        TAB_PROFILE -> ProfileScreen(
                            state,
                            onMyAds = { stack.add(Route.MyAds) },
                            onAddAd = { stack.add(Route.AddAd) },
                            onFavorites = { tab = TAB_FAV },
                            onTenders = { stack.add(Route.Tenders) },
                            onContractors = { stack.add(Route.Contractors) },
                            onBecome = {
                                val me = state.me
                                stack.add(if (me == null) Route.BecomeContractor else Route.ContractorDetail(me.id))
                            },
                            onNotifications = { stack.add(Route.Notifications) },
                            onToast = toast
                        )
                        TAB_FAV -> FavoritesScreen(state, openAd)
                        else -> MapScreen(state, openAd)
                    }
                }
            }
        }

        val top = stack.lastOrNull()
        if (top != null) {
            Surface(Modifier.fillMaxSize(), color = Color.White) {
                when (top) {
                    is Route.Detail -> DetailScreen(state, top.id, pop, toast)
                    is Route.Category -> {
                        val cat = categoryOf(top.id)
                        AdListScreen(
                            title = cat.title.replace("-", ""),
                            ads = state.cityAds().filter { it.categoryId == cat.id },
                            state = state,
                            emptyText = "В этой категории пока нет объявлений в г. ${state.city}",
                            onBack = pop,
                            onAd = openAd
                        )
                    }
                    Route.MyAds -> AdListScreen(
                        title = "Мои объявления",
                        ads = state.ads.filter { it.mine },
                        state = state,
                        emptyText = "Вы ещё ничего не публиковали",
                        onBack = pop,
                        onAd = openAd
                    )
                    Route.Tenders -> TendersScreen(
                        state = state,
                        onBack = pop,
                        onOpen = { stack.add(Route.TenderDetail(it)) },
                        onCreate = { stack.add(Route.CreateTender) }
                    )
                    is Route.TenderDetail -> TenderDetailScreen(
                        state = state, id = top.id, onBack = pop,
                        onBecome = { stack.add(Route.BecomeContractor) },
                        onToast = toast
                    )
                    Route.CreateTender -> CreateTenderScreen(
                        state = state,
                        onClose = pop,
                        onCreated = { n ->
                            pop()
                            toast(
                                if (n > 0) "Тендер опубликован. Уведомления отправлены подрядчикам (ИП, ООО, самозанятые): $n"
                                else "Тендер опубликован"
                            )
                        }
                    )
                    Route.Contractors -> ContractorsScreen(
                        state = state, onBack = pop,
                        onOpen = { stack.add(Route.ContractorDetail(it)) }
                    )
                    is Route.ContractorDetail -> ContractorDetailScreen(state, top.id, pop, toast)
                    Route.BecomeContractor -> BecomeContractorScreen(
                        state = state,
                        onClose = pop,
                        onDone = { legal ->
                            pop()
                            toast(
                                if (legal) "Вы зарегистрированы. Уведомления о новых тендерах включены"
                                else "Вы зарегистрированы как подрядчик"
                            )
                        }
                    )
                    Route.Notifications -> NotificationsScreen(
                        state = state, onBack = pop,
                        onTender = { stack.add(Route.TenderDetail(it)) }
                    )
                    Route.AddAd -> AddAdScreen(
                        state = state,
                        onClose = pop,
                        onPublished = {
                            pop()
                            tab = TAB_HOME
                            toast("Объявление опубликовано")
                        }
                    )
                }
            }
        }

        SnackbarHost(
            snackbar,
            Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = if (top == null) 80.dp else 72.dp)
        )
    }

    if (cityDialog) {
        AlertDialog(
            onDismissRequest = { cityDialog = false },
            title = { Text("Выберите город") },
            text = {
                Column {
                    cities.forEach { c ->
                        Row(
                            Modifier.clickable {
                                state.city = c
                                cityDialog = false
                            }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = c == state.city,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = Red)
                            )
                            Box(Modifier.width(10.dp))
                            Text(c)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { cityDialog = false }) { Text("Закрыть", color = Red) }
            },
            containerColor = Color.White
        )
    }
}
