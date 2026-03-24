package com.kkkk.moneysaving.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalScreenPadding = staticCompositionLocalOf { PaddingValues(0.dp) }
val LocalCurrencySymbol = staticCompositionLocalOf { "$" }
val LocalIsSyncEnabled = staticCompositionLocalOf { false }
