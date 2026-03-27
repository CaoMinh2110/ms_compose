package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsByCategoryIdUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(categoryId: String): Flow<List<Transaction>> {
        return repository.observeByCategoryId(categoryId)
    }
}
