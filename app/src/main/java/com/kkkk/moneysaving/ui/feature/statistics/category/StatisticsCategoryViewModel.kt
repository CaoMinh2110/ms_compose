package com.kkkk.moneysaving.ui.feature.statistics.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsByCategoryIdUseCase
import com.kkkk.moneysaving.ui.components.TransactionItemUI
import com.kkkk.moneysaving.ui.feature.home.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticsCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeTransactionsByCategoryIdUseCase: ObserveTransactionsByCategoryIdUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val categoryId: String = checkNotNull(savedStateHandle["categoryId"])
    private val selectedMonth =
        MutableStateFlow(YearMonth.parse(savedStateHandle.get<String>("selectedMonth")))

    val uiState: StateFlow<StatisticsCategoryUiState> = combine(
        observeTransactionsByCategoryIdUseCase.invoke(categoryId),
        selectedMonth
    ) { transactions, selectedMonth ->
        val category =
            categoryRepository.getById(categoryId) ?: return@combine StatisticsCategoryUiState()

        val allMonths = transactions.map {
            YearMonth.from(
                Instant.ofEpochMilli(it.occurredAt).atZone(ZoneId.systemDefault()).toLocalDate()
            )
        }.distinct().toMutableList()

        if (!allMonths.contains(selectedMonth)) {
            allMonths.add(selectedMonth)
        }
        val sortedMonths = allMonths.sorted()

        val chartData = sortedMonths.map { month ->
            val amount = transactions.filter {
                YearMonth.from(
                    Instant.ofEpochMilli(it.occurredAt).atZone(ZoneId.systemDefault()).toLocalDate()
                ) == month
            }.sumOf { kotlin.math.abs(it.amount) }
            ChartData(month, amount)
        }

        val filteredTransactions = transactions.filter {
            YearMonth.from(
                Instant.ofEpochMilli(it.occurredAt).atZone(ZoneId.systemDefault()).toLocalDate()
            ) == selectedMonth
        }

        val groupedItems = filteredTransactions.groupBy { t ->
            Instant.ofEpochMilli(t.occurredAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.mapValues { (_, list) ->
            list.map { it.toUI(category) }
        }

        StatisticsCategoryUiState(
            category = category,
            selectedMonth = selectedMonth,
            chartData = chartData,
            groupedItems = groupedItems
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsCategoryUiState()
    )

    fun selectMonth(month: YearMonth) {
        selectedMonth.update { month }
    }
}

data class StatisticsCategoryUiState(
    val category: Category? = null,
    val selectedMonth: YearMonth = YearMonth.now(),
    val chartData: List<ChartData> = emptyList(),
    val groupedItems: Map<LocalDate, List<TransactionItemUI>> = emptyMap(),
)

data class ChartData(
    val month: YearMonth,
    val amount: Long,
    var hasAnimated: Boolean = false
)
