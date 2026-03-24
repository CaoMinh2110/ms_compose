package com.kkkk.moneysaving

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kkkk.moneysaving.ui.LocalCurrencySymbol
import com.kkkk.moneysaving.ui.MainViewModel
import com.kkkk.moneysaving.ui.navigate.RootNavGraph
import com.kkkk.moneysaving.ui.theme.MoneySavingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = hiltViewModel()
            val symbol = viewModel.currencySymbol.collectAsState()

            MoneySavingTheme {
                CompositionLocalProvider(
                    LocalCurrencySymbol provides symbol.value,
                    LocalOverscrollFactory provides null
                ) {
                    RootNavGraph(navController = navController)
                }
            }
        }
    }
}

