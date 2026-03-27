package com.kkkk.moneysaving.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.repository.CurrencyRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    val languageCode: StateFlow<String> = settingsRepository.languageCode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "en"
        )

    val currencySymbol: StateFlow<String> = settingsRepository.currencyCode
        .map { code ->
            currencyRepository.getSupportedCurrencies()
                .find { it.code == code }?.symbol ?: "đ"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "đ"
        )

    val isSyncEnabled: StateFlow<Boolean> = settingsRepository.isSyncEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
}
