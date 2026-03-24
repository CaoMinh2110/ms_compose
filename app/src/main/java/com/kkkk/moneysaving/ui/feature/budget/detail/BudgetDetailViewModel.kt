package com.kkkk.moneysaving.ui.feature.budget.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.budget.CalculateBudgetUsageUseCase
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetByIdUseCase
import com.kkkk.moneysaving.domain.usecase.budget.SoftDeleteBudgetUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsByBudgetIdUseCase
import com.kkkk.moneysaving.ui.components.StatItemUI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class BudgetDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observerTransactionBudgetIdUseCase: ObserveTransactionsByBudgetIdUseCase,
    observerBudgetByIdUseCase: ObserverBudgetByIdUseCase,
    calculateBudgetUsageUseCase: CalculateBudgetUsageUseCase,
    private val softDeleteBudgetUseCase: SoftDeleteBudgetUseCase,
    private val categoryRepository: CategoryRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private val budgetId: String = checkNotNull(savedStateHandle["budgetId"])

    val uiState: StateFlow<BudgetDetailUiState> = combine(
        observerBudgetByIdUseCase.invoke(budgetId),
        observerTransactionBudgetIdUseCase.invoke(budgetId),
        calculateBudgetUsageUseCase.invoke(budgetId)
    ) { budget, transactions, used ->
        if (budget == null) {
            return@combine BudgetDetailUiState()
        }

        val categoryMap = transactions.mapNotNull { t ->
            val category = categoryRepository.getById(t.categoryId)
            category?.let { t to it }
        }

        val typeSums = mutableMapOf<String, Long>()
        val groupedItemsTemp = mutableMapOf<String, MutableList<StatItemUI>>()

        categoryMap
            .groupBy({ it.second.id }, { it })
            .forEach { (_, pairs) ->

                val category = pairs.first().second
                val totalAmount = pairs.sumOf { it.first.amount }
                val typeLabel = formatTypeLabel(category.type)

                typeSums[typeLabel] = (typeSums[typeLabel] ?: 0L) + totalAmount

                val item = StatItemUI(
                    id = category.id,
                    categoryName = category.name,
                    categoryColor = category.color,
                    amount = totalAmount,
                    process = if (budget.amount > 0) {
                        abs(totalAmount).toFloat() / budget.amount
                    } else 0f,
                    categoryIcon = category.icon
                )

                groupedItemsTemp
                    .getOrPut(typeLabel) { mutableListOf() }
                    .add(item)
            }

        val groupedItems = groupedItemsTemp
            .mapValues { it.value.toList() }
            .filterValues { it.isNotEmpty() }
        val groupedSummary = typeSums
            .mapValues { it.value }
            .filterValues { it != 0L }

        BudgetDetailUiState(
            budget = budget,
            groupedItems = groupedItems,
            groupedSummary = groupedSummary,
            progress = ((used + budget.amount) / budget.amount).toFloat().coerceIn(0f, 1f),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BudgetDetailUiState())

    fun delete(onBack: () -> Unit) {
        viewModelScope.launch {
            softDeleteBudgetUseCase.invoke(budgetId, System.currentTimeMillis())
            onBack()
        }
    }

    private fun formatTypeLabel(type: CategoryType): String {

        return when (type) {
            CategoryType.EXPENSE -> context.getString(R.string.home_total_expenditure)
            CategoryType.INCOME -> context.getString(R.string.home_total_income)
            else -> context.getString(R.string.home_total_loan)
        }
    }
}

data class BudgetDetailUiState(
    val budget: Budget = Budget(),
    val groupedItems: Map<String, List<StatItemUI>> = emptyMap(),
    val groupedSummary: Map<String, Long> = emptyMap(),
    val progress: Float = 0f
)
