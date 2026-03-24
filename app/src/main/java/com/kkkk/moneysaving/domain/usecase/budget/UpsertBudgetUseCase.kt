package com.kkkk.moneysaving.domain.usecase.budget

import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import javax.inject.Inject

class UpsertBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository,
) {
    suspend operator fun invoke(budget: Budget) {
        repository.upsert(budget)
    }
}

