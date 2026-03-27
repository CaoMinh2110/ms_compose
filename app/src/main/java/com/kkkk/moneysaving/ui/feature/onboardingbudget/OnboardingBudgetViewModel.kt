package com.kkkk.moneysaving.ui.feature.onboardingbudget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import com.kkkk.moneysaving.domain.usecase.budget.UpsertBudgetUseCase
import com.kkkk.moneysaving.ui.feature.budget.editor.DefaultBudgetColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OnboardingBudgetViewModel @Inject constructor(
    private val appStartRepository: AppStartRepository,
    private val upsertBudgetUseCase: UpsertBudgetUseCase
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
        val state = _uiState.value
        val name = state.name.trim()
        val amount = state.amount.toLongOrNull() ?: 0L
        if (name.isEmpty() || amount <= 0) return

        val now = System.currentTimeMillis()
        val color = DefaultBudgetColors.random()

        viewModelScope.launch {
            appStartRepository.setCompletedOnboarding(true)
            upsertBudgetUseCase(
                Budget(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    amount = amount,
                    color = color,
                    createdAt = now,
                    updatedAt = now,
                ),
            )

            onFinished()
        }
    }
}

data class OnboardingBudgetUiState(
    val name: String = "",
    val amount: String = "",
)

