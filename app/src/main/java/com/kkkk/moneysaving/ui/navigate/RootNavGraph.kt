package com.kkkk.moneysaving.ui.navigate

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kkkk.moneysaving.ui.feature.auth.AuthScreen
import com.kkkk.moneysaving.ui.feature.currency.CurrencyScreen
import com.kkkk.moneysaving.ui.feature.intro.IntroScreen
import com.kkkk.moneysaving.ui.feature.language.LanguageScreen
import com.kkkk.moneysaving.ui.feature.onboardingbudget.OnboardingBudgetScreen
import com.kkkk.moneysaving.ui.feature.splash.SplashScreen

@Composable
fun RootNavGraph(
    navController: NavHostController,
    viewModel: StartupViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = RootRoute.Splash,
    ) {
        composable(RootRoute.Splash) {
            SplashScreen(
                onFinished = { start ->
                    navController.navigate(start) {
                        popUpTo(RootRoute.Splash) { inclusive = true }
                    }
                },
                viewModel = viewModel,
            )
        }
        composable(RootRoute.Intro) {
            IntroScreen(
                onFinished = {
                    navController.navigate(RootRoute.Language) {
                        popUpTo(RootRoute.Intro) { inclusive = true }
                    }
                },
            )
        }
        composable(RootRoute.Language) {
            LanguageScreen(
                onContinue = {
                    navController.navigate(RootRoute.Currency) {
                        popUpTo(RootRoute.Language) { inclusive = true }
                    }
                },
            )
        }
        composable(RootRoute.Currency) {
            CurrencyScreen(
                onContinue = {
                    navController.navigate(RootRoute.Auth) {
                        popUpTo(RootRoute.Currency) { inclusive = true }
                    }
                },
            )
        }
        composable(RootRoute.Auth) {
            AuthScreen(
                onFinished = { isLoggedIn ->
                    val next = if (isLoggedIn) RootRoute.Main else RootRoute.OnboardingBudget
                    navController.navigate(next) {
                        popUpTo(RootRoute.Auth) { inclusive = true }
                    }
                },
            )
        }
        composable(RootRoute.OnboardingBudget) {
            OnboardingBudgetScreen(
                onFinished = {
                    navController.navigate(RootRoute.Main) {
                        popUpTo(RootRoute.OnboardingBudget) { inclusive = true }
                    }
                },
            )
        }
        composable(RootRoute.Main) {
            MainNavGraph()
        }
    }
}
