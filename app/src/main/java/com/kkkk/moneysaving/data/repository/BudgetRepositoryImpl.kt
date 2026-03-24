package com.kkkk.moneysaving.data.repository

import com.kkkk.moneysaving.data.local.dao.BudgetDao
import com.kkkk.moneysaving.data.local.entity.BudgetEntity
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao,
) : BudgetRepository {
    override fun observeAll(): Flow<List<Budget>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Budget?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun upsert(budget: Budget) {
        dao.upsert(budget.toEntity())
    }

    override suspend fun upsertAll(budgets: List<Budget>) {
        dao.upsertAll(budgets.map { it.toEntity() })
    }

    override suspend fun softDelete(id: String, updatedAt: Long) {
        dao.softDelete(id = id, updatedAt = updatedAt)
    }
}

private fun BudgetEntity.toDomain(): Budget {
    return Budget(
        id = id,
        name = name,
        amount = amount,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )
}

private fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = id,
        name = name,
        amount = amount,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
    )
}

