package com.kkkk.moneysaving.ui.feature.statistics.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetsUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class StatisticsBalanceViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    observerBudgetsUseCase: ObserverBudgetsUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val selectedYear = MutableStateFlow(LocalDate.now().year)

    val uiState: StateFlow<StatisticsBalanceUiState> = combine(
        selectedYear,
        observeTransactionsUseCase(),
        observerBudgetsUseCase()
    ) { year, transactions, budgets ->

        val totalBudget = budgets.sumOf { it.amount }

        val incomeAll = transactions.filter { t ->
            categoryRepository.getType(t.categoryId) == CategoryType.INCOME
        }.sumOf { it.amount }
        val expenseAll = transactions.filter { t ->
            categoryRepository.getType(t.categoryId) == CategoryType.EXPENSE
        }.sumOf { abs(it.amount) }
        val borrowInAll = transactions.filter { t ->
            t.categoryId == "borrow_in"
        }.sumOf { abs(it.amount) }
        val loanOutAll = transactions.filter { t ->
            t.categoryId == "loan_out"
        }.sumOf { abs(it.amount) }

        val currentBalance = totalBudget + incomeAll + borrowInAll - expenseAll - loanOutAll

        val yearTransactions = transactions.filter { t ->
            val date =
                Instant.ofEpochMilli(t.occurredAt).atZone(ZoneId.systemDefault()).toLocalDate()
            date.year == year
        }

        val totalYearExpense = yearTransactions.filter {
            categoryRepository.getType(it.categoryId) == CategoryType.EXPENSE
        }.sumOf { abs(it.amount) }

        val totalYearIncome = yearTransactions.filter {
            categoryRepository.getType(it.categoryId) == CategoryType.INCOME
        }.sumOf { abs(it.amount) }

        val monthlyStats = (1..12).map { month ->
            val monthTransactions = yearTransactions.filter { t ->
                val date =
                    Instant.ofEpochMilli(t.occurredAt).atZone(ZoneId.systemDefault()).toLocalDate()
                date.monthValue == month
            }

            val expense =
                monthTransactions.filter { categoryRepository.getType(it.categoryId) == CategoryType.EXPENSE }
                    .sumOf { abs(it.amount) }
            val income =
                monthTransactions.filter { categoryRepository.getType(it.categoryId) == CategoryType.INCOME }
                    .sumOf { abs(it.amount) }
            val loan =
                monthTransactions.filter { it.categoryId == "loan_out" }.sumOf { abs(it.amount) }
            val borrow =
                monthTransactions.filter { it.categoryId == "borrow_in" }.sumOf { abs(it.amount) }

            MonthlyStat(
                monthName = LocalDate.of(year, month, 1).month.getDisplayName(
                    TextStyle.FULL,
                    Locale.ENGLISH
                ),
                expense = expense,
                income = income,
                loan = loan,
                borrow = borrow,
                balance = income + borrow - expense - loan
            )
        }.filter { it.expense != 0L || it.income != 0L || it.loan != 0L || it.borrow != 0L }
            .reversed()

        StatisticsBalanceUiState(
            totalBalance = currentBalance,
            totalExpense = totalYearExpense,
            totalIncome = totalYearIncome,
            selectedYear = year,
            monthlyStats = monthlyStats
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsBalanceUiState())

    fun selectYear(year: Int) {
        selectedYear.update { year }
    }
}

data class StatisticsBalanceUiState(
    val totalBalance: Long = 0L,
    val totalExpense: Long = 0L,
    val totalIncome: Long = 0L,
    val selectedYear: Int = LocalDate.now().year,
    val monthlyStats: List<MonthlyStat> = emptyList()
)

data class MonthlyStat(
    val monthName: String,
    val expense: Long,
    val income: Long,
    val loan: Long,
    val borrow: Long,
    val balance: Long
)
