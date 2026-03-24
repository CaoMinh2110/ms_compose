package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTransactionByIdUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(id: String): Flow<Transaction?> = repository.observeById(id)
}
