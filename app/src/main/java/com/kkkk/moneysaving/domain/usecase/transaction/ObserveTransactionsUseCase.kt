package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(): Flow<List<Transaction>> = repository.observeAll()
}

