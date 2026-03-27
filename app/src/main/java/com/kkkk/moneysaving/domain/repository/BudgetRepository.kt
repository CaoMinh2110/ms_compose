package com.kkkk.moneysaving.domain.repository

import com.kkkk.moneysaving.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeAll(): Flow<List<Budget>>
    fun observeById(id: String): Flow<Budget?>
    suspend fun upsert(budget: Budget)
    suspend fun upsertAll(budgets: List<Budget>)
    suspend fun softDelete(id: String, updatedAt: Long)
    suspend fun hardDelete(id: String)
}
