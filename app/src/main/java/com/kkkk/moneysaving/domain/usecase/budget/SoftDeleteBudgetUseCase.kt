package com.kkkk.moneysaving.domain.usecase.budget

import com.kkkk.moneysaving.domain.repository.BudgetRepository
import javax.inject.Inject

class SoftDeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository,
) {
    suspend operator fun invoke(id: String, updatedAt: Long) {
        repository.softDelete(id = id, updatedAt = updatedAt)
    }
}

