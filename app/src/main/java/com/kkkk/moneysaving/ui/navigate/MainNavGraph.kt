package com.kkkk.moneysaving.ui.navigate

import androidx.compose.animation.AnimatedContent
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import java.time.YearMonth

private const val DURATION = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val mainRoutes = listOf(
        Route.Home::class,
        Route.Statistics::class,
        Route.Budget::class,
        Route.Settings::class
    )

    val showBottomBar = mainRoutes.any { routeClass ->
        currentDestination?.hasRoute(routeClass) == true
    }

    val bottomItems = listOf(
        BottomItem(
            route = Route.Home,
            labelRes = R.string.title_home,
            icon = Icons.Rounded.Home,
            index = 0
        ),
        BottomItem(
            route = Route.Statistics,
            labelRes = R.string.title_statistics,
            icon = Icons.Rounded.PieChart,
            index = 1
        ),
        BottomItem(
            route = null,
            labelRes = 0,
            isPlaceholder = true
        ),
        BottomItem(
            route = Route.Budget,
            labelRes = R.string.title_budget,
            icon = R.drawable.ic_budget,
            index = 3
        ),
        BottomItem(
            route = Route.Settings,
            labelRes = R.string.title_settings,
            icon = Icons.Rounded.Settings,
            index = 4
        ),
    )

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .drawBehind {
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
                                NavigationBarItem(
                                    selected = false,
                                    onClick = {},
                                    icon = {},
                                    enabled = false
                                )
                            } else {
                                val isSelected = item.route?.let { route ->
                                    currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
                                } ?: false

                                val alpha by animateFloatAsState(
                                    if (isSelected) 1f else 0f,
                                    tween(DURATION),
                                    label = "home_alpha"
                                )

                                NavigationBarItem(
                                    selected = isSelected,
                                    enabled = !isSelected,
                                    colors = NavigationBarItemDefaults.colors(
                                        unselectedIconColor = TextSecondary,
                                        unselectedTextColor = TextSecondary,
                                        disabledIconColor = Secondary,
                                        disabledTextColor = Secondary,
                                        indicatorColor = Color.Unspecified
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
                                                        indication = null
                                                    ) {
                                                        item.route?.let { route ->
                                                            navController.navigate(route) {
                                                                popUpTo(navController.graph.findStartDestination().id) {
                                                                    saveState = true
                                                                }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                    },
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                val spacerHeight by animateDpAsState(
                                                    targetValue = if (isSelected) 4.dp else 0.dp,
                                                    animationSpec = tween(durationMillis = DURATION)
                                                )

                                                when (item.icon) {
                                                    is ImageVector -> Icon(
                                                        imageVector = item.icon,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )

                                                    is Int -> Icon(
                                                        painter = painterResource(item.icon),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(spacerHeight))

                                                AnimatedContent(
                                                    targetState = isSelected,
                                                    transitionSpec = {
                                                        (slideInVertically(
                                                            initialOffsetY = { it },
                                                            animationSpec = tween(DURATION)
                                                        ) + fadeIn(animationSpec = tween(DURATION)))
                                                            .togetherWith(
                                                                (slideOutVertically(
                                                                    targetOffsetY = { -it },
                                                                    animationSpec = tween(
                                                                        DURATION
                                                                    )
                                                                ) + fadeOut(
                                                                    animationSpec = tween(
                                                                        DURATION
                                                                    )
                                                                ))
                                                            )
                                                    },
                                                    label = "nav_label"
                                                ) {
                                                    Text(
                                                        text = if (it) stringResource(item.labelRes) else "",
                                                        style = MaterialTheme.typography.bodySmall,
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.drawBehind {
                                        if (alpha > 0f) {
                                            drawLine(
                                                color = Secondary.copy(alpha = alpha),
                                                start = Offset(size.width / 6, 0f),
                                                end = Offset(size.width * 5 / 6, 0f),
                                                strokeWidth = 6.dp.toPx()
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    FloatingActionButton(
                        onClick = { navController.navigate(Route.TransactionEditor()) },
                        shape = RoundedCornerShape(12.dp),
                        containerColor = Secondary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
        },
    ) { padding ->
        CompositionLocalProvider(LocalScreenPadding provides padding) {
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = Route.Home,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        val initialRoute = initialState.destination
                        val targetRoute = targetState.destination

                        val isInitialMain = isMainRoute(initialRoute)
                        val isTargetMain = isMainRoute(targetRoute)

                        when {
                            isInitialMain && isTargetMain -> {
                                val initialIndex = getRouteIndex(initialRoute)
                                val targetIndex = getRouteIndex(targetRoute)
                                if (initialIndex < targetIndex) {
                                    slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(DURATION)
                                    ) + fadeIn()
                                } else {
                                    slideInHorizontally(
                                        initialOffsetX = { -it },
                                        animationSpec = tween(DURATION)
                                    ) + fadeIn()
                                }
                            }

                            isInitialMain && !isTargetMain -> {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(DURATION)
                                ) + fadeIn()
                            }

                            else -> {
                                slideInHorizontally(
                                    initialOffsetX = { it },
                                    animationSpec = tween(DURATION)
                                ) + fadeIn()
                            }
                        }
                    },
                    exitTransition = {
                        val initialRoute = initialState.destination
                        val targetRoute = targetState.destination

                        val isInitialMain = isMainRoute(initialRoute)
                        val isTargetMain = isMainRoute(targetRoute)

                        // Disable exit transition when going to FullscreenImageView
                        if (targetRoute.hasRoute<Route.FullscreenImageView>()) {
                            ExitTransition.None
                        } else {
                            when {
                                isInitialMain && isTargetMain -> {
                                    val initialIndex = getRouteIndex(initialRoute)
                                    val targetIndex = getRouteIndex(targetRoute)
                                    if (initialIndex < targetIndex) {
                                        slideOutHorizontally(
                                            targetOffsetX = { -it },
                                            animationSpec = tween(DURATION)
                                        ) + fadeOut()
                                    } else {
                                        slideOutHorizontally(
                                            targetOffsetX = { it },
                                            animationSpec = tween(DURATION)
                                        ) + fadeOut()
                                    }
                                }

                                isInitialMain && !isTargetMain -> {
                                    slideOutHorizontally(
                                        targetOffsetX = { -it / 3 },
                                        animationSpec = tween(DURATION)
                                    ) + fadeOut()
                                }

                                else -> {
                                    slideOutHorizontally(
                                        targetOffsetX = { -it },
                                        animationSpec = tween(DURATION)
                                    ) + fadeOut()
                                }
                            }
                        }
                    },
                    popEnterTransition = {
                        val initialRoute = initialState.destination
                        val targetRoute = targetState.destination

                        val isTargetMain = isMainRoute(targetRoute)

                        // Disable popEnter when returning from FullscreenImageView
                        if (initialRoute.hasRoute<Route.FullscreenImageView>()) {
                            EnterTransition.None
                        } else {
                            when {
                                isTargetMain -> {
                                    slideInHorizontally(
                                        initialOffsetX = { -it / 3 },
                                        animationSpec = tween(DURATION)
                                    ) + fadeIn()
                                }

                                else -> {
                                    slideInHorizontally(
                                        initialOffsetX = { -it },
                                        animationSpec = tween(DURATION)
                                    ) + fadeIn()
                                }
                            }
                        }
                    },
                    popExitTransition = {
                        val targetRoute = targetState.destination
                        if (targetRoute.hasRoute<Route.Account>()) {
                            ExitTransition.None
                        } else {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(DURATION)
                            ) + fadeOut()
                        }
                    }
                ) {
                    composable<Route.Home> {
                        HomeScreen(
                            onOpenSearch = { navController.navigate(Route.TransactionSearch) },
                            onOpenTransactionDetail = {
                                navController.navigate(Route.TransactionDetail(it))
                            },
                        )
                    }
                    composable<Route.Statistics> {
                        StatisticsScreen(
                            onBalanceClick = { navController.navigate(Route.StatisticsBalance) },
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
                    composable<Route.TransactionEditor> {
                        TransactionEditorScreen(onBack = { navController.popBackStack() })
                    }
                    composable<Route.Budget>(
                        exitTransition = {
                            if (targetState.destination.hasRoute<Route.BudgetDetail>()) {
                                fadeOut(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        },
                        popEnterTransition = {
                            if (initialState.destination.hasRoute<Route.BudgetDetail>()) {
                                fadeIn(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        }
                    ) {
                        BudgetListScreen(
                            onOpenDetail = { navController.navigate(Route.BudgetDetail(it)) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                    composable<Route.Settings> {
                        SettingsScreen(
                            onAccountClick = { navController.navigate(Route.Account) },
                            onLanguageClick = { navController.navigate(Route.SettingsLanguage) },
                            onCurrencyClick = { navController.navigate(Route.SettingsCurrency) },
                        )
                    }

                    composable<Route.TransactionSearch> {
                        TransactionSearchScreen(
                            onBack = { navController.popBackStack() },
                            onOpenDetail = { navController.navigate(Route.TransactionDetail(it)) },
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

                    composable<Route.BudgetDetail>(
                        enterTransition = {
                            if (initialState.destination.hasRoute<Route.Budget>()) {
                                fadeIn(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        },
                        popExitTransition = {
                            if (targetState.destination.hasRoute<Route.Budget>()) {
                                fadeOut(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        }
                    ) {
                        BudgetDetailScreen(
                            onBack = { navController.popBackStack() },
                            onCategoryDetail = {
                                navController.navigate(
                                    Route.StatisticsCategory(it)
                                )
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    composable<Route.Account> {
                        AccountScreen(
                            onBack = { navController.popBackStack() },
                            onViewFullscreenImage = { url ->
                                navController.navigate(Route.FullscreenImageView(url))
                            },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                    composable<Route.SettingsLanguage> {
                        SettingsLanguageScreen(onBack = { navController.popBackStack() })
                    }
                    composable<Route.SettingsCurrency> {
                        SettingsCurrencyScreen(onBack = { navController.popBackStack() })
                    }

                    composable<Route.StatisticsBalance> {
                        StatisticsBalanceScreen(onBack = { navController.popBackStack() })
                    }
                    composable<Route.StatisticsCategory> {
                        StatisticsCategoryScreen(
                            onBack = { navController.popBackStack() },
                            onTransactionClick = {
                                navController.navigate(Route.TransactionDetail(it))
                            }
                        )
                    }
                    composable<Route.FullscreenImageView> { backStackEntry ->
                        val route: Route.FullscreenImageView = backStackEntry.toRoute()
                        val decodedUrl =
                            URLDecoder.decode(route.imageUrl, StandardCharsets.UTF_8.toString())
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

private fun isMainRoute(destination: androidx.navigation.NavDestination?): Boolean {
    return destination?.hasRoute<Route.Home>() == true ||
            destination?.hasRoute<Route.Statistics>() == true ||
            destination?.hasRoute<Route.Budget>() == true ||
            destination?.hasRoute<Route.Settings>() == true
}

private fun getRouteIndex(destination: androidx.navigation.NavDestination?): Int {
    return when {
        destination?.hasRoute<Route.Home>() == true -> 0
        destination?.hasRoute<Route.Statistics>() == true -> 1
        destination?.hasRoute<Route.Budget>() == true -> 3
        destination?.hasRoute<Route.Settings>() == true -> 4
        else -> 5
    }
}

private data class BottomItem(
    val route: Route?,
    val labelRes: Int,
    val icon: Any? = null,
    val index: Int? = null,
    val isPlaceholder: Boolean = false
)
