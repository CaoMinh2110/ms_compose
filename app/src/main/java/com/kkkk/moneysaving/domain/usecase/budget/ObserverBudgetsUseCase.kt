package com.kkkk.moneysaving.domain.usecase.budget

import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserverBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository,
) {
    operator fun invoke(): Flow<List<Budget>> = repository.observeAll()
}

