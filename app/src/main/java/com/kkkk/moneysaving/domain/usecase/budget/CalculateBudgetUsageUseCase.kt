package com.kkkk.moneysaving.domain.usecase.budget

import com.kkkk.moneysaving.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CalculateBudgetUsageUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(budgetId: String?, endAt: Long? = null): Flow<Long> {
        return transactionRepository.sumAmountByBudgetAndRange(
            budgetId = budgetId,
            endAt = endAt,
        )
    }
}

