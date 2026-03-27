package com.kkkk.moneysaving.ui.feature.transaction.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.repository.CategoryRepository
import com.kkkk.moneysaving.domain.usecase.transaction.ObserveTransactionsUseCase
import com.kkkk.moneysaving.ui.components.TransactionItemUI
import com.kkkk.moneysaving.ui.feature.home.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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
                val categoryName = category?.name.orEmpty()
                categoryName.contains(trimmed, ignoreCase = true) ||
                        (t.note.orEmpty()).contains(trimmed, ignoreCase = true)
            }
        }
        TransactionSearchUiState(
            query = q,
            items = filtered.map { it.toUI(categoryRepository.getById(it.categoryId)!!) },
        )
    }.stateIn(
        viewModelScope,
        kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5_000),
        TransactionSearchUiState()
    )

    fun updateQuery(value: String) {
        query.update { value }
    }
}

data class TransactionSearchUiState(
    val query: String = "",
    val items: List<TransactionItemUI> = emptyList(),
)

