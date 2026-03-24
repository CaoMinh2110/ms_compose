package com.kkkk.moneysaving.data.repository

import com.kkkk.moneysaving.data.local.dao.TransactionDao
import com.kkkk.moneysaving.data.local.entity.TransactionEntity
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
) : TransactionRepository {
    override fun observeAll(): Flow<List<Transaction>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Transaction?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun observeByBudgetId(budgetId: String): Flow<List<Transaction>>  =
        dao.observeByBudgetId(budgetId).map { list -> list.map { it.toDomain() } }

    override fun observeByTimeRange(endAt: Long): Flow<List<Transaction>> =
        dao.observeByTimeRange(endAt).map { list -> list.map { it.toDomain() } }

    override suspend fun upsert(transaction: Transaction) {
        dao.upsert(transaction.toEntity())
    }

    override suspend fun upsertAll(transactions: List<Transaction>) {
        dao.upsertAll(transactions.map { it.toEntity() })
    }

    override suspend fun softDelete(id: String, updatedAt: Long) {
        dao.softDelete(id = id, updatedAt = updatedAt)
    }

    override fun sumAmountByBudgetAndRange(
        budgetId: String?,
        endAt: Long?
    ): Flow<Long> {
        return dao.sumAmountByBudgetAndRange(budgetId, endAt)
    }
}

private fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        categoryId = categoryId,
        budgetId = budgetId,
        amount = amount,
        note = note,
        occurredAt = occurredAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )
}

private fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        categoryId = categoryId,
        budgetId = budgetId,
        amount = amount,
        note = note,
        occurredAt = occurredAt,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )
}
