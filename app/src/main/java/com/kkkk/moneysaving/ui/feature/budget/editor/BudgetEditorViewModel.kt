package com.kkkk.moneysaving.ui.feature.budget.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetByIdUseCase
import com.kkkk.moneysaving.domain.usecase.budget.UpsertBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BudgetEditorViewModel @Inject constructor(
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
    private val observerBudgetByIdUseCase: ObserverBudgetByIdUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BudgetEditorUiState())
    val uiState: StateFlow<BudgetEditorUiState> = _uiState.asStateFlow()

    private var editingBudget: Budget? = null

    fun loadBudget(budgetId: String?) {
        if (budgetId == null) {
            editingBudget = null
            _uiState.update { BudgetEditorUiState() }
            return
        }
        viewModelScope.launch {
            val budget = observerBudgetByIdUseCase(budgetId).first()
            if (budget != null) {
                editingBudget = budget
                _uiState.update {
                    it.copy(
                        name = budget.name,
                        amount = budget.amount.toString()
                    )
                }
            }
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun updateAmount(value: String) {
        _uiState.update { it.copy(amount = value.filter { c -> c.isDigit() }) }
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val name = state.name.trim()
        val amount = state.amount.toLongOrNull() ?: 0L
        if (name.isEmpty() || amount <= 0) return

        val now = System.currentTimeMillis()

        viewModelScope.launch {
            val budget = editingBudget?.copy(
                name = name,
                amount = amount,
                updatedAt = now
            ) ?: Budget(
                id = UUID.randomUUID().toString(),
                name = name,
                amount = amount,
                color = DefaultBudgetColors.random(),
                createdAt = now,
                updatedAt = now,
            )

            upsertBudgetUseCase(budget)
            onSaved()
            _uiState.update { BudgetEditorUiState() }
            editingBudget = null
        }
    }
}

data class BudgetEditorUiState(
    val name: String = "",
    val amount: String = "",
)

object DefaultBudgetColors {
    private val list = listOf(
        0xFFFDCC1E,
        0xFFFDAA48,
        0xFF5EA5E7,
        0xFFEF7FC5,
        0xFF64D852,
        0xFFE19DDF,
        0xFF84D8BA,
        0xFFF5D358,
        0xFFFC8484,
        0xFF6DC3C3,
        0xFFAA8EFB,
        0xFF7499FC,
        0xFFE2BA28,
        0xFF73C766,
        0xFF7DBFFB,
        0xFFA083F6,
        0xFFBB6BD9,
        0xFF7499FC,
        0xFF4F80FC,
        0xFFAB4FFC,
    )

    fun random(): Long {
        return list.random()
    }
}
