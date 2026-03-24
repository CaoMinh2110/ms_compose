package com.kkkk.moneysaving.domain.usecase.budget

import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserverBudgetByIdUseCase @Inject constructor(
    private val repository: BudgetRepository,
) {
    operator fun invoke(id: String): Flow<Budget?> = repository.observeById(id)
}
