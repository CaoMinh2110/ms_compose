package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsByBudgetIdUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(budgetId: String): Flow<List<Transaction>> = repository.observeByBudgetId(budgetId)
}
