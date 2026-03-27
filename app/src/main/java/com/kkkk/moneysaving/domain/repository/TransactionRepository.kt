package com.kkkk.moneysaving.domain.repository

import com.kkkk.moneysaving.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeAll(): Flow<List<Transaction>>
    fun observeById(id: String): Flow<Transaction?>
    fun observeByBudgetId(budgetId: String): Flow<List<Transaction>>
    fun observeByTimeRange(endAt: Long): Flow<List<Transaction>>
    fun observeByCategoryId(categoryId: String): Flow<List<Transaction>>
    fun sumAmountByCategoryAndRange(categoryId: String, startAt: Long, endAt: Long): Flow<Long>
    suspend fun upsert(transaction: Transaction)
    suspend fun upsertAll(transactions: List<Transaction>)
    suspend fun softDelete(id: String, updatedAt: Long)
    suspend fun hardDelete(id: String)
    fun sumAmountByBudgetAndRange(budgetId: String?, endAt: Long?): Flow<Long>
}
