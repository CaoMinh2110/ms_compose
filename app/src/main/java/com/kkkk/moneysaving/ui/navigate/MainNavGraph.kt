package com.kkkk.moneysaving.ui.navigate

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.feature.budget.detail.BudgetDetailScreen
import com.kkkk.moneysaving.ui.feature.budget.list.BudgetListScreen
import com.kkkk.moneysaving.ui.feature.home.HomeScreen
import com.kkkk.moneysaving.ui.feature.settings.SettingsScreen
import com.kkkk.moneysaving.ui.feature.settings.account.AccountScreen
import com.kkkk.moneysaving.ui.feature.settings.account.FullscreenImageViewScreen
import com.kkkk.moneysaving.ui.feature.settings.currency.SettingsCurrencyScreen
import com.kkkk.moneysaving.ui.feature.settings.language.SettingsLanguageScreen
import com.kkkk.moneysaving.ui.feature.statistics.balance.StatisticsBalanceScreen
import com.kkkk.moneysaving.ui.feature.statistics.category.StatisticsCategoryScreen
import com.kkkk.moneysaving.ui.feature.statistics.overview.StatisticsScreen
import com.kkkk.moneysaving.ui.feature.transaction.detail.TransactionDetailScreen
import com.kkkk.moneysaving.ui.feature.transaction.editor.TransactionEditorScreen
import com.kkkk.moneysaving.ui.feature.transaction.search.TransactionSearchScreen
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextSecondary
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass

private const val ANIM_DURATION = 300

private val MAIN_ROUTES: List<KClass<*>> = listOf(
    Route.Home::class,
    Route.Statistics::class,
    Route.Budget::class,
    Route.Settings::class
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            MainBottomBar(
                navController = navController,
                currentDestination = currentDestination
            )
        },
    ) { padding ->
        CompositionLocalProvider(LocalScreenPadding provides padding) {
            SharedTransitionLayout {

                NavHost(
                    navController = navController,
                    startDestination = Route.Home,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = { enterTransition() },
                    exitTransition = { exitTransition() },
                    popEnterTransition = { popEnterTransition() },
                    popExitTransition = { popExitTransition() }
                ) {

                    // =========================================================
                    // 🏠 HOME FEATURE
                    // =========================================================
                    composable<Route.Home> {
                        HomeScreen(
                            onOpenSearch = {
                                navController.navigate(Route.TransactionSearch)
                            },
                            onOpenTransactionDetail = {
                                navController.navigate(Route.TransactionDetail(it))
                            },
                        )
                    }

                    // =========================================================
                    // 📊 STATISTICS FEATURE
                    // =========================================================
                    composable<Route.Statistics> {
                        StatisticsScreen(
                            onBalanceClick = {
                                navController.navigate(Route.StatisticsBalance)
                            },
                            onCategoryClick = { categoryId, month ->
                                navController.navigate(
                                    Route.StatisticsCategory(
                                        categoryId,
                                        month.toString()
                                    )
                                )
                            },
                        )
                    }

                    composable<Route.StatisticsBalance> {
                        StatisticsBalanceScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<Route.StatisticsCategory> {
                        StatisticsCategoryScreen(
                            onBack = { navController.popBackStack() },
                            onTransactionClick = {
                                navController.navigate(Route.TransactionDetail(it))
                            }
                        )
                    }

                    // =========================================================
                    // 💰 TRANSACTION FEATURE
                    // =========================================================
                    composable<Route.TransactionSearch> {
                        TransactionSearchScreen(
                            onBack = { navController.popBackStack() },
                            onOpenDetail = {
                                navController.navigate(Route.TransactionDetail(it))
                            },
                        )
                    }

                    composable<Route.TransactionDetail> {
                        TransactionDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = {
                                navController.navigate(Route.TransactionEditor(it))
                            },
                        )
                    }

                    composable<Route.TransactionEditor> {
                        TransactionEditorScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // =========================================================
                    // 🧾 BUDGET FEATURE
                    // =========================================================
                    composable<Route.Budget>(
                        exitTransition = {
                            if (targetState.destination.hasRoute<Route.BudgetDetail>()) {
                                fadeOut(animationSpec = tween(ANIM_DURATION))
                            } else null
                        },
                        popEnterTransition = {
                            if (initialState.destination.hasRoute<Route.BudgetDetail>()) {
                                fadeIn(animationSpec = tween(ANIM_DURATION))
                            } else null
                        }
                    ) {
                        BudgetListScreen(
                            onOpenDetail = {
                                navController.navigate(Route.BudgetDetail(it))
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    composable<Route.BudgetDetail>(
                        enterTransition = {
                            if (initialState.destination.hasRoute<Route.Budget>()) {
                                fadeIn(animationSpec = tween(ANIM_DURATION))
                            } else null
                        },
                        popExitTransition = {
                            if (targetState.destination.hasRoute<Route.Budget>()) {
                                fadeOut(animationSpec = tween(ANIM_DURATION))
                            } else null
                        }
                    ) {
                        BudgetDetailScreen(
                            onBack = { navController.popBackStack() },
                            onCategoryDetail = {
                                navController.navigate(Route.StatisticsCategory(it))
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    // =========================================================
                    // ⚙️ SETTINGS FEATURE
                    // =========================================================
                    composable<Route.Settings> {
                        SettingsScreen(
                            onAccountClick = {
                                navController.navigate(Route.Account)
                            },
                            onLanguageClick = {
                                navController.navigate(Route.SettingsLanguage)
                            },
                            onCurrencyClick = {
                                navController.navigate(Route.SettingsCurrency)
                            },
                        )
                    }

                    composable<Route.SettingsLanguage> {
                        SettingsLanguageScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<Route.SettingsCurrency> {
                        SettingsCurrencyScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable<Route.Account> {
                        AccountScreen(
                            onBack = { navController.popBackStack() },
                            onViewFullscreenImage = { url ->
                                navController.navigate(
                                    Route.FullscreenImageView(url)
                                )
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    composable<Route.FullscreenImageView> { backStackEntry ->
                        val route: Route.FullscreenImageView =
                            backStackEntry.toRoute()

                        val decodedUrl = URLDecoder.decode(
                            route.imageUrl,
                            StandardCharsets.UTF_8.toString()
                        )

                        FullscreenImageViewScreen(
                            imageUrl = decodedUrl,
                            onBack = { navController.popBackStack() },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MainBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val bottomItems = remember {
        listOf(
            BottomItem(route = Route.Home, labelRes = R.string.title_home, icon = Icons.Rounded.Home),
            BottomItem(route = Route.Statistics, labelRes = R.string.title_statistics, icon = Icons.Rounded.PieChart),
            BottomItem(isPlaceholder = true),
            BottomItem(route = Route.Budget, labelRes = R.string.title_budget, icon = R.drawable.ic_budget),
            BottomItem(route = Route.Settings, labelRes = R.string.title_settings, icon = Icons.Rounded.Settings),
        )
    }

    val isVisible = isMainRoute(currentDestination)

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    ) {
        Box(contentAlignment = Alignment.Center) {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            ) {
                bottomItems.forEach { item ->
                    if (item.isPlaceholder) {
                        NavigationBarPlaceholder()
                    } else {
                        val isSelected = item.route?.let { route ->
                            currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
                        } ?: false

                        MainBottomNavItem(
                            item = item,
                            isSelected = isSelected,
                            onItemClick = {
                                item.route?.let { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }

            MainFloatingActionButton(
                onClick = { navController.navigate(Route.TransactionEditor()) }
            )
        }
    }
}

@Composable
private fun RowScope.NavigationBarPlaceholder() {
    NavigationBarItem(
        selected = false,
        onClick = {},
        icon = {},
        enabled = false
    )
}

@Composable
private fun RowScope.MainBottomNavItem(
    item: BottomItem,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(ANIM_DURATION),
        label = "nav_item_alpha"
    )

    NavigationBarItem(
        selected = isSelected,
        enabled = !isSelected,
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = TextSecondary,
            unselectedTextColor = TextSecondary,
            disabledIconColor = Secondary,
            disabledTextColor = Secondary,
            indicatorColor = Color.Transparent
        ),
        onClick = {},
        icon = {
            Box(
                modifier = Modifier.height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onItemClick
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val spacerHeight by animateDpAsState(
                        targetValue = if (isSelected) 4.dp else 0.dp,
                        animationSpec = tween(durationMillis = ANIM_DURATION),
                        label = "nav_item_spacer"
                    )

                    MainNavItemIcon(icon = item.icon)

                    Spacer(modifier = Modifier.height(spacerHeight))

                    MainNavItemLabel(labelRes = item.labelRes, isSelected = isSelected)
                }
            }
        },
        modifier = Modifier.drawBehind {
            if (indicatorAlpha > 0f) {
                drawLine(
                    color = Secondary.copy(alpha = indicatorAlpha),
                    start = Offset(size.width / 6, 0f),
                    end = Offset(size.width * 5 / 6, 0f),
                    strokeWidth = 6.dp.toPx()
                )
            }
        }
    )
}

@Composable
private fun MainNavItemIcon(icon: Any?) {
    when (icon) {
        is ImageVector -> Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        is Int -> Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun MainNavItemLabel(labelRes: Int?, isSelected: Boolean) {
    AnimatedContent(
        targetState = isSelected,
        transitionSpec = {
            val enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(ANIM_DURATION)) +
                    fadeIn(animationSpec = tween(ANIM_DURATION))
            val exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(ANIM_DURATION)) +
                    fadeOut(animationSpec = tween(ANIM_DURATION))
            enter togetherWith exit
        },
        label = "nav_label"
    ) { targetIsSelected ->
        if (targetIsSelected && labelRes != null && labelRes != 0) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun MainFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        containerColor = Secondary,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = Modifier.size(50.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
    val initialRoute = initialState.destination
    val targetRoute = targetState.destination

    return if (isMainRoute(initialRoute) && isMainRoute(targetRoute)) {
        val initialIndex = getRouteIndex(initialRoute)
        val targetIndex = getRouteIndex(targetRoute)
        val offset = if (initialIndex < targetIndex) { it: Int -> it } else { it: Int -> -it }
        slideInHorizontally(initialOffsetX = offset, animationSpec = tween(ANIM_DURATION)) + fadeIn()
    } else {
        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn()
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
    val initialRoute = initialState.destination
    val targetRoute = targetState.destination

    if (targetRoute.hasRoute<Route.FullscreenImageView>()) return ExitTransition.None

    return when {
        isMainRoute(initialRoute) && isMainRoute(targetRoute) -> {
            val initialIndex = getRouteIndex(initialRoute)
            val targetIndex = getRouteIndex(targetRoute)
            val offset = if (initialIndex < targetIndex) { it: Int -> -it } else { it: Int -> it }
            slideOutHorizontally(targetOffsetX = offset, animationSpec = tween(ANIM_DURATION)) + fadeOut()
        }
        isMainRoute(initialRoute) && !isMainRoute(targetRoute) -> {
            slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(ANIM_DURATION)) + fadeOut()
        }
        else -> {
            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut()
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
    val initialRoute = initialState.destination
    val targetRoute = targetState.destination

    if (initialRoute.hasRoute<Route.FullscreenImageView>()) return EnterTransition.None

    return if (isMainRoute(targetRoute)) {
        slideInHorizontally(initialOffsetX = { -it / 3 }, animationSpec = tween(ANIM_DURATION)) + fadeIn()
    } else {
        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn()
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
    if (targetState.destination.hasRoute<Route.Account>()) return ExitTransition.None
    return slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut()
}

private fun isMainRoute(destination: NavDestination?): Boolean {
    return MAIN_ROUTES.any { destination?.hasRoute(it) == true }
}

private fun getRouteIndex(destination: NavDestination?): Int {
    return when {
        destination?.hasRoute<Route.Home>() == true -> 0
        destination?.hasRoute<Route.Statistics>() == true -> 1
        destination?.hasRoute<Route.Budget>() == true -> 3
        destination?.hasRoute<Route.Settings>() == true -> 4
        else -> 5
    }
}

private data class BottomItem(
    val route: Route? = null,
    val labelRes: Int? = null,
    val icon: Any? = null,
    val isPlaceholder: Boolean = false
)
