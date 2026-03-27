package com.kkkk.moneysaving.ui.navigate

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Statistics : Route

    @Serializable
    data object Budget : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data class TransactionEditor(val transactionId: String? = null) : Route

    @Serializable
    data object TransactionSearch : Route

    @Serializable
    data class TransactionDetail(val transactionId: String) : Route

    @Serializable
    data class BudgetDetail(val budgetId: String) : Route

    @Serializable
    data object Account : Route

    @Serializable
    data object SettingsLanguage : Route

    @Serializable
    data object SettingsCurrency : Route

    @Serializable
    data object StatisticsBalance : Route

    @Serializable
    data class StatisticsCategory(val categoryId: String, val selectedMonth: String? = null) : Route

    @Serializable
    data class FullscreenImageView(val imageUrl: String) : Route
}

@Serializable
sealed interface RootRoute {
    @Serializable
    data object Splash : RootRoute

    @Serializable
    data object Intro : RootRoute

    @Serializable
    data object Language : RootRoute

    @Serializable
    data object Currency : RootRoute

    @Serializable
    data object Auth : RootRoute

    @Serializable
    data object OnboardingBudget : RootRoute

    @Serializable
    data object Main : RootRoute
}
