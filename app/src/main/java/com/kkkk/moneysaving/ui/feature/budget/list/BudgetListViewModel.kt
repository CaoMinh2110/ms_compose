package com.kkkk.moneysaving.ui.feature.budget.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.usecase.budget.CalculateBudgetUsageUseCase
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    observerBudgetsUseCase: ObserverBudgetsUseCase,
    private val calculateBudgetUsageUseCase: CalculateBudgetUsageUseCase,
) : ViewModel() {

    private val _currentBudgetId = MutableStateFlow<String?>(null)
    val currentBudgetId: StateFlow<String?> = _currentBudgetId

    val uiState: StateFlow<BudgetListUiState> = observerBudgetsUseCase()
        .flatMapLatest { budgets ->
            if (budgets.isEmpty()) {
                flowOf(BudgetListUiState())
            } else {
                val spentFlows = budgets.map { budget ->
                    calculateBudgetUsageUseCase(budget.id).map { spentSum ->
                        val remainingAmount = budget.amount + spentSum

                        BudgetItemUiState(
                            budget = budget,
                            spentAmount = spentSum,
                            percent = if (budget.amount > 0)
                                (remainingAmount.toFloat() / budget.amount).coerceIn(0f, 1f)
                            else 0f
                        )
                    }
                }

                combine(spentFlows) { itemsArray ->
                    val items = itemsArray.toList()
                    val totalBudget = items.sumOf { it.budget.amount }
                    val totalSpent = items.sumOf { it.spentAmount }

                    val totalSpentAbs = abs(totalSpent.coerceAtMost(0L))

                    val totalSpentPercent = if (totalBudget > 0)
                        ((totalSpentAbs.toDouble() / totalBudget) * 100).toInt().coerceIn(0, 100)
                    else 0

                    BudgetListUiState(
                        items = items,
                        totalBudget = totalBudget,
                        totalSpent = totalSpentAbs,
                        totalRemainingPercent = (100 - totalSpentPercent).coerceAtLeast(0)
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BudgetListUiState()
        )

    fun updateBudgetId(id: String?) {
        _currentBudgetId.value = id
    }
}

data class BudgetListUiState(
    val items: List<BudgetItemUiState> = emptyList(),
    val totalBudget: Long = 0,
    val totalSpent: Long = 0,
    val totalRemainingPercent: Int = 0
)

data class BudgetItemUiState(
    val budget: Budget,
    val spentAmount: Long,
    val percent: Float,
    var hasAnimated: Boolean = false
)
