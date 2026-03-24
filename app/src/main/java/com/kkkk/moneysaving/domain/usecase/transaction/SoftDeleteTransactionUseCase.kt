package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.repository.TransactionRepository
import javax.inject.Inject

class SoftDeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(id: String, updatedAt: Long) {
        repository.softDelete(id = id, updatedAt = updatedAt)
    }
}

