package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import javax.inject.Inject

class UpsertTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.upsert(transaction)
    }
}

