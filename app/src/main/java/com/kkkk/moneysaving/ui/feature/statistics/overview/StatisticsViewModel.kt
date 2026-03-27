package com.kkkk.moneysaving.ui.feature.statistics.overview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetsUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsByTimeRangeUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsUseCase
import com.kkkk.moneysaving.ui.components.StatItemUI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val observeTransactionsByTimeRangeUseCase: ObserveTransactionsByTimeRangeUseCase,
    private val observerBudgetsUseCase: ObserverBudgetsUseCase,
    private val categoryRepository: CategoryRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val selectedTime = MutableStateFlow(YearMonth.now())
    private val selectedType = MutableStateFlow(CategoryType.EXPENSE)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatisticsUiState> = combine(
        selectedTime.flatMapLatest { time ->
            val endOfMonth = time.atEndOfMonth().atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            observeTransactionsByTimeRangeUseCase(endOfMonth)
        },
        selectedType,
        observerBudgetsUseCase(),
        observeTransactionsUseCase()
    ) { transactionsByTime, currentType, budgets, allTransactions ->

        val totalBudget = budgets.sumOf { it.amount }
        val income = allTransactions.filter { t ->
            categoryRepository.getType(t.categoryId) == CategoryType.INCOME
        }.sumOf { it.amount }
        val expense = allTransactions.filter { t ->
            categoryRepository.getType(t.categoryId) == CategoryType.EXPENSE
        }.sumOf { abs(it.amount) }
        val borrowIn = allTransactions.filter { t ->
            t.categoryId == "borrow_in"
        }.sumOf { abs(it.amount) }
        val loanOut = allTransactions.filter { t ->
            t.categoryId == "loan_out"
        }.sumOf { abs(it.amount) }

        val balance = totalBudget + income + borrowIn - expense - loanOut

        val categoryMap = transactionsByTime.mapNotNull { t ->
            val category = categoryRepository.getById(t.categoryId)
            category?.let { t to it }
        }

        val filteredData = categoryMap.filter { it.second.type == currentType }

        val totalAmountForType = filteredData.sumOf { abs(it.first.amount) }

        val groupedItems = filteredData
            .groupBy({ it.second.id }, { it })
            .map { (_, pairs) ->
                val category = pairs.first().second
                val totalAmount = pairs.sumOf { it.first.amount }

                StatItemUI(
                    categoryId = category.id,
                    categoryName = category.name,
                    categoryColor = category.color,
                    amount = totalAmount,
                    process = if (totalAmountForType > 0) {
                        abs(totalAmount).toFloat() / totalAmountForType
                    } else 0f,
                    categoryIcon = category.icon
                )
            }
            .sortedByDescending { abs(it.amount) }

        StatisticsUiState(
            balance = balance,
            selectedTime = selectedTime.value,
            selectedType = currentType,
            totalAmount = totalAmountForType,
            groupedItems = groupedItems
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())

    fun selectTime(time: YearMonth) {
        selectedTime.update { time }
    }

    fun selectType(type: CategoryType) {
        selectedType.update { type }
    }
}

data class StatisticsUiState(
    val balance: Long = 0L,
    val selectedTime: YearMonth = YearMonth.now(),
    val selectedType: CategoryType = CategoryType.EXPENSE,
    val totalAmount: Long = 0L,
    val groupedItems: List<StatItemUI> = emptyList(),
)
