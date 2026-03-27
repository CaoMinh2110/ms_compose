package com.kkkk.moneysaving.ui.feature.transaction.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import com.kkkk.moneysaving.domain.usecase.transaction.SoftDeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transactionRepository: TransactionRepository,
    budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val softDeleteTransactionUseCase: SoftDeleteTransactionUseCase,
) : ViewModel() {
    private val transactionId: String = checkNotNull(savedStateHandle["transactionId"])

    val uiState: StateFlow<TransactionDetailUiState> = combine(
        transactionRepository.observeById(transactionId),
        budgetRepository.observeAll(),
    ) { transaction, budgets ->
        val category = transaction?.let { categoryRepository.getById(it.categoryId) }
        val budget = transaction?.budgetId?.let { id -> budgets.firstOrNull { it.id == id } }
        TransactionDetailUiState(
            transaction = transaction,
            category = category,
            budget = budget,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TransactionDetailUiState())

    fun delete(onBack: () -> Unit) {
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            softDeleteTransactionUseCase(transactionId, now)
            onBack()
        }
    }
}

data class TransactionDetailUiState(
    val transaction: Transaction? = null,
    val category: Category? = null,
    val budget: Budget? = null,
)

