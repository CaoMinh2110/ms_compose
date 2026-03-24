package com.kkkk.moneysaving.ui.feature.budget.list

import androidx.compose.ui.util.fastCoerceAtMost
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.usecase.budget.CalculateBudgetUsageUseCase
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    observerBudgetsUseCase: ObserverBudgetsUseCase,
    private val calculateBudgetUsageUseCase: CalculateBudgetUsageUseCase,
) : ViewModel() {

    private val _monthOffset = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BudgetListUiState> = combine(
        observerBudgetsUseCase(),
        _monthOffset
    ) { budgets, offset ->
        budgets to offset
    }.flatMapLatest { (budgets, offset) ->
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, offset)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endAt = calendar.timeInMillis

        if (budgets.isEmpty()) {
            flowOf(BudgetListUiState())
        } else {
            val spentFlows = budgets.map { budget ->
                calculateBudgetUsageUseCase(budget.id, endAt).map { spentSum ->
                    val remainingAmount = spentSum + budget.amount
                    BudgetItemUiState(
                        budget = budget,
                        spentAmount = spentSum,
                        percent = if (budget.amount > 0) (remainingAmount / budget.amount).toFloat() else 0f
                    )
                }
            }
            combine(spentFlows) { itemsArray ->
                val items = itemsArray.toList()
                val totalBudget = items.sumOf { it.budget.amount }
                val totalSpent = items.sumOf { it.spentAmount }
                val totalPercent = if (totalBudget > 0) (totalSpent.toDouble() / totalBudget * 100).toInt() else 0

                BudgetListUiState(
                    items = items,
                    totalBudget = totalBudget,
                    totalSpent = totalSpent.fastCoerceAtMost(0L),
                    totalRemainingPercent = (100 - totalPercent).coerceAtLeast(0)
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BudgetListUiState())

    fun updateMonthOffset(offset: Int) {
        _monthOffset.value = offset
    }
}

data class BudgetListUiState(
    val items: List<BudgetItemUiState> = emptyList(),
    val totalBudget: Long = 0,
    val totalSpent: Long = 0,
    val totalRemainingPercent: Int = 0,
)

data class BudgetItemUiState(
    val budget: Budget,
    val spentAmount: Long,
    val percent: Float,
    var hasAnimated: Boolean = false
)
