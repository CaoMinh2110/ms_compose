package com.kkkk.moneysaving.ui.feature.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Currency
import com.kkkk.moneysaving.domain.repository.CurrencyRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    currencyRepository: CurrencyRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        val supported = currencyRepository.getSupportedCurrencies()
        _uiState.update { it.copy(currencies = supported) }
        viewModelScope.launch {
            settingsRepository.currencyCode.collect { code ->
                _uiState.update { s -> s.copy(selectedCode = code) }
            }
        }
    }

    fun select(code: String) {
        _uiState.update { it.copy(selectedCode = code) }
        viewModelScope.launch {
            settingsRepository.setCurrencyCode(code)
        }
    }

    fun persistSelection() {
        viewModelScope.launch {
            settingsRepository.setCurrencyCode(uiState.value.selectedCode)
        }
    }
}

data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),
    val selectedCode: String = "USD",
)
