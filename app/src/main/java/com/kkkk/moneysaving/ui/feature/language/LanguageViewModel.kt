package com.kkkk.moneysaving.ui.feature.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Language
import com.kkkk.moneysaving.domain.repository.LanguageRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val languageRepository: LanguageRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        val supported = languageRepository.getSupportedLanguages()
        _uiState.update { it.copy(languages = supported) }
        viewModelScope.launch {
            settingsRepository.languageCode.collect { code ->
                _uiState.update { s -> s.copy(selectedCode = code) }
            }
        }
    }

    fun select(code: String) {
        _uiState.update { it.copy(selectedCode = code) }
    }

    fun persistSelection() {
        val code = _uiState.value.selectedCode
        viewModelScope.launch { settingsRepository.setLanguageCode(code) }
    }
}

data class LanguageUiState(
    val languages: List<Language> = emptyList(),
    val selectedCode: String = "en",
)

