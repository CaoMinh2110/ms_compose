package com.kkkk.moneysaving.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kkkk.moneysaving.data.local.dao.BudgetDao
import com.kkkk.moneysaving.data.local.dao.DeletedItemDao
import com.kkkk.moneysaving.data.local.entity.BudgetEntity
import com.kkkk.moneysaving.data.local.entity.DeletedItemEntity
import com.kkkk.moneysaving.data.local.entity.DeletedItemType
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao,
    private val deletedItemDao: DeletedItemDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : BudgetRepository {
    override fun observeAll(): Flow<List<Budget>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Budget?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun upsert(budget: Budget) {
        dao.upsert(budget.toEntity())
        syncToRemote(budget)
    }

    override suspend fun upsertAll(budgets: List<Budget>) {
        dao.upsertAll(budgets.map { it.toEntity() })
        budgets.forEach { syncToRemote(it) }
    }

    override suspend fun softDelete(id: String, updatedAt: Long) {
        dao.softDelete(id = id, updatedAt = updatedAt)
        val budget = dao.observeById(id).first()?.toDomain()
        budget?.let { syncToRemote(it.copy(isDeleted = true, updatedAt = updatedAt)) }
    }

    override suspend fun hardDelete(id: String) {
        dao.hardDelete(id)
    }

    private suspend fun syncToRemote(budget: Budget) {
        val isSyncEnabled = settingsRepository.isSyncEnabled.first()
        val user = authRepository.userProfile.first()
        if (isSyncEnabled && user != null) {
            val docRef = firestore.collection("users")
                .document(user.uid)
                .collection("budgets")
                .document(budget.id)

            try {
                if (budget.isDeleted) {
                    docRef.delete().await()
                    deletedItemDao.insert(DeletedItemEntity(budget.id, DeletedItemType.BUDGET))
                } else {
                    docRef.set(budget).await()
                }
            } catch (e: Exception) {
                // Log error
            }
        } else if (budget.isDeleted) {
            deletedItemDao.insert(DeletedItemEntity(budget.id, DeletedItemType.BUDGET))
        }
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
