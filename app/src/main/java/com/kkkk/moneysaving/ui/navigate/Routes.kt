package com.kkkk.moneysaving.ui.navigate

object RootRoute {
    const val Splash = "splash"
    const val Intro = "intro"
    const val Language = "language"
    const val Currency = "currency"
    const val Auth = "auth"
    const val OnboardingBudget = "onboarding_budget"
    const val Main = "main"
}

object MainRoute {
    const val Home = "home"
    const val Statistics = "statistics"
    const val TransactionEditor = "transaction_editor"
    const val Budget = "budget"
    const val Settings = "settings"
}

object SubRoute {
    const val TransactionSearch = "transaction_search"
    const val TransactionDetail = "transaction_detail/{transactionId}"
    fun transactionDetail(transactionId: String) = "transaction_detail/$transactionId"

    const val BudgetDetail = "budget_detail/{budgetId}"
    fun budgetDetail(budgetId: String) = "budget_detail/$budgetId"

    const val Account = "account"
    const val SettingsLanguage = "settings_language"
    const val SettingsCurrency = "settings_currency"
}
