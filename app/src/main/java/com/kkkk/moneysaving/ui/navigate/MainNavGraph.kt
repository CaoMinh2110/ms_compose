package com.kkkk.moneysaving.ui.navigate

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.ui.LocalScreenPadding
import com.kkkk.moneysaving.ui.feature.budget.detail.BudgetDetailScreen
import com.kkkk.moneysaving.ui.feature.budget.list.BudgetListScreen
import com.kkkk.moneysaving.ui.feature.home.HomeScreen
import com.kkkk.moneysaving.ui.feature.settings.SettingsScreen
import com.kkkk.moneysaving.ui.feature.settings.account.AccountScreen
import com.kkkk.moneysaving.ui.feature.settings.currency.SettingsCurrencyScreen
import com.kkkk.moneysaving.ui.feature.settings.language.SettingsLanguageScreen
import com.kkkk.moneysaving.ui.feature.statistics.StatisticsScreen
import com.kkkk.moneysaving.ui.feature.transaction.detail.TransactionDetailScreen
import com.kkkk.moneysaving.ui.feature.transaction.editor.TransactionEditorScreen
import com.kkkk.moneysaving.ui.feature.transaction.search.TransactionSearchScreen
import com.kkkk.moneysaving.ui.theme.Secondary
import com.kkkk.moneysaving.ui.theme.TextSecondary

private const val DURATION = 300

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val mainRoutes = listOf(
        MainRoute.Home,
        MainRoute.Statistics,
        MainRoute.Budget,
        MainRoute.Settings
    )

    val showBottomBar = currentRoute in mainRoutes

    val bottomItems = listOf(
        BottomItem(
            route = MainRoute.Home,
            labelRes = R.string.nav_home,
            icon = Icons.Default.Home,
            index = 0
        ),
        BottomItem(
            route = MainRoute.Statistics,
            labelRes = R.string.nav_statistics,
            icon = Icons.Default.BarChart,
            index = 1
        ),
        BottomItem(
            route = "",
            labelRes = 0,
            icon = Icons.Default.Add,
            index = 2,
            isPlaceholder = true
        ),
        BottomItem(
            route = MainRoute.Budget,
            labelRes = R.string.nav_budget,
            icon = Icons.Default.PieChart,
            index = 3
        ),
        BottomItem(
            route = MainRoute.Settings,
            labelRes = R.string.nav_settings,
            icon = Icons.Default.Settings,
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
                                val isSelected =
                                    backStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true

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
                                                        indication = null
                                                    ) {
                                                        navController.navigate(item.route) {
                                                            popUpTo(navController.graph.findStartDestination().id) {
                                                                saveState = true
                                                            }
                                                            launchSingleTop = true
                                                            restoreState = true
                                                        }
                                                    },
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                val spacerHeight by animateDpAsState(
                                                    targetValue = if (isSelected) 4.dp else 0.dp,
                                                    animationSpec = tween(durationMillis = DURATION)
                                                )

                                                Icon(
                                                    imageVector = item.icon,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(24.dp)
                                                )

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
                        onClick = { navController.navigate(MainRoute.TransactionEditor) },
                        shape = CircleShape,
                        containerColor = Secondary,
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        modifier = Modifier.size(60.dp)
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
                    startDestination = MainRoute.Home,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        val initialIndex = getRouteIndex(initialState.destination.route)
                        val targetIndex = getRouteIndex(targetState.destination.route)
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
                    },
                    exitTransition = {
                        val initialIndex = getRouteIndex(initialState.destination.route)
                        val targetIndex = getRouteIndex(targetState.destination.route)
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
                ) {
                    composable(MainRoute.Home) {
                        HomeScreen(
                            onOpenSearch = { navController.navigate(SubRoute.TransactionSearch) },
                            onOpenTransactionDetail = { id ->
                                navController.navigate(SubRoute.transactionDetail(id))
                            },
                        )
                    }
                    composable(MainRoute.Statistics) { StatisticsScreen() }
                    composable(MainRoute.TransactionEditor) {
                        TransactionEditorScreen(
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable(
                        route = MainRoute.Budget,
                        exitTransition = {
                            if (targetState.destination.route == SubRoute.BudgetDetail) {
                                fadeOut(animationSpec = tween(DURATION))
                            } else {
                                null // Dùng animation mặc định của NavHost
                            }
                        },
                        popEnterTransition = {
                            if (initialState.destination.route == SubRoute.BudgetDetail) {
                                fadeIn(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        }
                    ) {
                        BudgetListScreen(
                            onOpenDetail = { id -> navController.navigate(SubRoute.budgetDetail(id)) },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                    composable(MainRoute.Settings) {
                        SettingsScreen(
                            onAccountClick = { navController.navigate(SubRoute.Account) },
                            onLanguageClick = { navController.navigate(SubRoute.SettingsLanguage) },
                            onCurrencyClick = { navController.navigate(SubRoute.SettingsCurrency) },
                        )
                    }

                    composable(SubRoute.TransactionSearch) {
                        TransactionSearchScreen(
                            onBack = { navController.popBackStack() },
                            onOpenDetail = { id -> navController.navigate(SubRoute.transactionDetail(id)) },
                        )
                    }
                    composable(SubRoute.TransactionDetail) {
                        TransactionDetailScreen(
                            onBack = { navController.popBackStack() },
                            onEdit = { navController.navigate(MainRoute.TransactionEditor) },
                        )
                    }

                    composable(
                        route = SubRoute.BudgetDetail,
                        enterTransition = {
                            if (initialState.destination.route == MainRoute.Budget) {
                                fadeIn(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        },
                        popExitTransition = {
                            if (targetState.destination.route == MainRoute.Budget) {
                                fadeOut(animationSpec = tween(DURATION))
                            } else {
                                null
                            }
                        }
                    ) {
                        BudgetDetailScreen(
                            onBack = { navController.popBackStack() },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    composable(SubRoute.Account) {
                        AccountScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(SubRoute.SettingsLanguage) {
                        SettingsLanguageScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable(SubRoute.SettingsCurrency) {
                        SettingsCurrencyScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

private fun getRouteIndex(route: String?): Int {
    return when (route) {
        MainRoute.Home -> 0
        MainRoute.Statistics -> 1
        MainRoute.Budget -> 3
        MainRoute.Settings -> 4
        else -> 5
    }
}

private data class BottomItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector,
    val index: Int,
    val isPlaceholder: Boolean = false
)
