package com.kkkk.moneysaving.ui.feature.onboardingbudget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingBudgetViewModel @Inject constructor(
    private val appStartRepository: AppStartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingBudgetUiState())
    val uiState: StateFlow<OnboardingBudgetUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateAmount(amount: String) {
        _uiState.update { it.copy(amount = amount) }
    }

    fun finish(onFinished: () -> Unit) {
        viewModelScope.launch {
            appStartRepository.setCompletedOnboarding(true)
            onFinished()
        }
    }
}

data class OnboardingBudgetUiState(
    val name: String = "",
    val amount: String = "",
)

