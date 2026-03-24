package com.kkkk.moneysaving.ui.feature.transaction.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.CategoryType
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.budget.ObserverBudgetsUseCase
import com.kkkk.moneysaving.domain.usecase.transaction.UpsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransactionEditorViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val upsertTransactionUseCase: UpsertTransactionUseCase,
    private val observerBudgetsUseCase: ObserverBudgetsUseCase
) : ViewModel() {
    private val allCategory = categoryRepository.getAll()

    private val _uiState = MutableStateFlow(TransactionEditorUiState(selectedCategoryId = allCategory[0].id))
    val uiState: StateFlow<TransactionEditorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val budgets = observerBudgetsUseCase().first()

            _uiState.update { state ->
                state.copy(
                    allBudget = budgets,
                    filteredCategories = allCategory.filter {
                        it.type == state.selectedType
                    },
                    isLoading = false
                )
            }
        }
    }

    fun selectType(type: CategoryType) {
        _uiState.update {
            it.copy(
                selectedType = type,
                filteredCategories = allCategory.filter { c -> c.type == type })
        }
    }

    fun updateAmount(value: String) {
        _uiState.update { it.copy(amount = value) }
    }

    fun selectCategory(id: String) {
        _uiState.update { it.copy(selectedCategoryId = id) }
    }

    fun updateNote(value: String) {
        _uiState.update { it.copy(note = value) }
    }

    fun updateBorrower(value: String) {
        _uiState.update { it.copy(borrower = value) }
    }

    fun updateTime(time: LocalTime) {
        _uiState.update { it.copy(selectedTime = LocalDateTime.of(it.selectedTime.toLocalDate(), time)) }
    }

    fun updateDate(date: LocalDate) {
        _uiState.update { it.copy(selectedTime = LocalDateTime.of(date, it.selectedTime.toLocalTime())) }
    }

    fun save(budgetId: String?, onSaved: () -> Unit) {
        val state = _uiState.value
        val category = state.selectedCategoryId.let { categoryRepository.getById(it) } ?: return
        val amountValue = state.amount.toLongOrNull() ?: 0L
        if (amountValue == 0L) return

        val now = System.currentTimeMillis()
        val signedAmount = amountValue.toSignedAmount(category)
        val note = buildNote(category.type, state.note, state.borrower)

        viewModelScope.launch {
            upsertTransactionUseCase(
                Transaction(
                    id = UUID.randomUUID().toString(),
                    categoryId = category.id,
                    budgetId = budgetId,
                    amount = signedAmount,
                    note = note,
                    occurredAt = now,
                    createdAt = now,
                    updatedAt = now,
                    isDeleted = false,
                ),
            )
            onSaved()
        }
    }
}

data class TransactionEditorUiState(
    val selectedType: CategoryType = CategoryType.EXPENSE,
    val amount: String = "0",
    val note: String = "",
    val borrower: String = "",
    val selectedTime: LocalDateTime = LocalDateTime.now(),
    val selectedCategoryId: String = "",
    val selectedBudgetId: String? = null,
    val allBudget: List<Budget> = emptyList(),
    val filteredCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false
)

private fun Long.toSignedAmount(category: Category): Long {
    return when (category.id) {
        "borrow_in" -> kotlin.math.abs(this)
        else -> when (category.type) {
            CategoryType.INCOME -> kotlin.math.abs(this)
            CategoryType.EXPENSE -> -kotlin.math.abs(this)
            CategoryType.LOAN -> -kotlin.math.abs(this)
        }
    }
}

private fun buildNote(type: CategoryType, note: String, borrower: String): String? {
    val trimmedNote = note.trim().takeIf { it.isNotEmpty() }
    return if (type == CategoryType.LOAN) {
        val trimmedBorrower = borrower.trim().takeIf { it.isNotEmpty() }
        listOfNotNull(trimmedBorrower, trimmedNote).takeIf { it.isNotEmpty() }?.joinToString(" - ")
    } else {
        trimmedNote
    }
}

