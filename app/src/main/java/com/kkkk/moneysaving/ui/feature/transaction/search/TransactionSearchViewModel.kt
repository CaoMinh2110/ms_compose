package com.kkkk.moneysaving.ui.feature.transaction.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.Category
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class TransactionSearchViewModel @Inject constructor(
    observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")

    val uiState: StateFlow<TransactionSearchUiState> = combine(
        observeTransactionsUseCase(),
        query,
    ) { transactions, q ->
        val trimmed = q.trim()
        val filtered = if (trimmed.isEmpty()) {
            transactions
        } else {
            transactions.filter { t ->
                val category = categoryRepository.getById(t.categoryId)
                val categoryName = category?.name ?: ""
                categoryName.contains(trimmed, ignoreCase = true) ||
                    (t.note ?: "").contains(trimmed, ignoreCase = true)
            }
        }
        TransactionSearchUiState(
            query = q,
            items = filtered.map { it.toItem(categoryRepository.getById(it.categoryId)) },
        )
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000), TransactionSearchUiState())

    fun updateQuery(value: String) {
        query.update { value }
    }
}

data class TransactionSearchUiState(
    val query: String = "",
    val items: List<TransactionSearchItem> = emptyList(),
)

data class TransactionSearchItem(
    val id: String,
    val categoryName: String,
    val note: String?,
    val amount: Long,
    val occurredAt: Long,
    val categoryColor: Long,
)

private fun Transaction.toItem(category: Category?): TransactionSearchItem {
    return TransactionSearchItem(
        id = id,
        categoryName = category?.name ?: "",
        note = note,
        amount = amount,
        occurredAt = occurredAt,
        categoryColor = category?.color ?: 0xFFEAF4F7,
    )
}

