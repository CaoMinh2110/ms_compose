package com.kkkk.moneysaving.domain.usecase.transaction

import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsByTimeRangeUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(endAt: Long): Flow<List<Transaction>> = repository.observeByTimeRange(endAt)
}
