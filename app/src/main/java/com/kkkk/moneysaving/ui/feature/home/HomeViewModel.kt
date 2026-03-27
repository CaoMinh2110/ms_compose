package com.kkkk.moneysaving.ui.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.R
import com.kkkk.moneysaving.data.repository.CategoryRepositoryImpl.Companion.ID_BORROW_IN
import com.kkkk.moneysaving.data.repository.CategoryRepositoryImpl.Companion.ID_LOAN_OUT
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetsUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsByTimeRangeUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.SoftDeleteTransactionUseCase
import com.kkkk.moneysaving.ui.components.TransactionItemUI
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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeTransactions: ObserveTransactionsUseCase,
    observerBudgetsUseCase: ObserverBudgetsUseCase,
    private val observeTransactionsByTimeRangeUseCase: ObserveTransactionsByTimeRangeUseCase,
    private val softDeleteTransactionUseCase: SoftDeleteTransactionUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val selectedType = MutableStateFlow(CategoryType.EXPENSE)
    private val selectedTime = MutableStateFlow(YearMonth.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        selectedTime.flatMapLatest { time ->
            val endOfMonth = time.atEndOfMonth().atTime(23, 59, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            observeTransactionsByTimeRangeUseCase(endOfMonth)
        },
        selectedType,
        observerBudgetsUseCase(),
        observeTransactions()
    ) { transactionsByTime, type, budgets, allTransactions ->

        val totalBudget = budgets.sumOf { it.amount }
        val income = allTransactions.filter { t ->
            categoryRepository.getType(t.categoryId) == CategoryType.INCOME
        }.sumOf { it.amount }
        val expense = allTransactions.filter { t ->
            categoryRepository.getType(t.categoryId) == CategoryType.EXPENSE
        }.sumOf { kotlin.math.abs(it.amount) }
        val borrowIn = allTransactions.filter { t ->
            t.categoryId == ID_BORROW_IN
        }.sumOf { kotlin.math.abs(it.amount) }
        val loanOut = allTransactions.filter { t ->
            t.categoryId == ID_LOAN_OUT
        }.sumOf { kotlin.math.abs(it.amount) }

        val balance = totalBudget + income + borrowIn - expense - loanOut

        val displayItems = transactionsByTime.filter { t ->
            categoryRepository.getType(t.categoryId) == type
        }

        val totalAmount = when (type) {
            CategoryType.EXPENSE -> displayItems.sumOf { if (it.amount < 0) kotlin.math.abs(it.amount) else 0L }
            CategoryType.INCOME -> displayItems.sumOf { if (it.amount > 0) it.amount else 0L }
            CategoryType.LOAN -> displayItems.sumOf { kotlin.math.abs(it.amount) }
        }

        val groupedItems = displayItems.groupBy { t ->
            Instant.ofEpochMilli(t.occurredAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.mapValues { (_, list) ->
            list.map { it.toUI(categoryRepository.getById(it.categoryId)!!) }
        }

        HomeUiState(
            balance = balance,
            selectedType = type,
            selectedTime = selectedTime.value,
            totalAmount = totalAmount,
            groupedItems = groupedItems,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())



    fun selectType(type: CategoryType) {
        selectedType.update { type }
    }

    fun selectTime(time: YearMonth) {
        selectedTime.update { time }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            softDeleteTransactionUseCase.invoke(id, System.currentTimeMillis())
        }
    }
}

data class HomeUiState(
    val balance: Long = 0L,
    val selectedType: CategoryType = CategoryType.EXPENSE,
    val selectedTime: YearMonth = YearMonth.now(),
    val totalAmount: Long = 0L,
    val groupedItems: Map<LocalDate, List<TransactionItemUI>> = emptyMap(),
)

fun Transaction.toUI(category: Category): TransactionItemUI {
    return TransactionItemUI(
        id = id,
        categoryName = category.name,
        note = note,
        amount = amount,
        occurredAt = occurredAt,
        categoryIcon = category.icon
    )
}
