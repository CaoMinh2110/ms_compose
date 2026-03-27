package com.kkkk.moneysaving.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kkkk.moneysaving.data.local.dao.DeletedItemDao
import com.kkkk.moneysaving.data.local.dao.TransactionDao
import com.kkkk.moneysaving.data.local.entity.DeletedItemEntity
import com.kkkk.moneysaving.data.local.entity.DeletedItemType
import com.kkkk.moneysaving.data.local.entity.TransactionEntity
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao,
    private val deletedItemDao: DeletedItemDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : TransactionRepository {
    override fun observeAll(): Flow<List<Transaction>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Transaction?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun observeByBudgetId(budgetId: String): Flow<List<Transaction>>  =
        dao.observeByBudgetId(budgetId).map { list -> list.map { it.toDomain() } }

    override fun observeByTimeRange(endAt: Long): Flow<List<Transaction>> =
        dao.observeByTimeRange(endAt).map { list -> list.map { it.toDomain() } }

    override fun observeByCategoryId(categoryId: String): Flow<List<Transaction>> =
        dao.observeByCategoryId(categoryId).map { list -> list.map { it.toDomain() } }

    override fun sumAmountByCategoryAndRange(
        categoryId: String,
        startAt: Long,
        endAt: Long
    ): Flow<Long> = dao.sumAmountByCategoryAndRange(categoryId, startAt, endAt)

    override suspend fun upsert(transaction: Transaction) {
        dao.upsert(transaction.toEntity())
        syncToRemote(transaction)
    }

    override suspend fun upsertAll(transactions: List<Transaction>) {
        dao.upsertAll(transactions.map { it.toEntity() })
        transactions.forEach { syncToRemote(it) }
    }

    override suspend fun softDelete(id: String, updatedAt: Long) {
        dao.softDelete(id = id, updatedAt = updatedAt)
        val transaction = dao.observeById(id).first()?.toDomain()
        transaction?.let { syncToRemote(it.copy(isDeleted = true, updatedAt = updatedAt)) }
    }

    override suspend fun hardDelete(id: String) {
        dao.hardDelete(id)
    }

    override fun sumAmountByBudgetAndRange(
        budgetId: String?,
        endAt: Long?
    ): Flow<Long> {
        return dao.sumAmountByBudgetAndRange(budgetId, endAt)
    }

    private suspend fun syncToRemote(transaction: Transaction) {
        val isSyncEnabled = settingsRepository.isSyncEnabled.first()
        val user = authRepository.userProfile.first()
        
        if (isSyncEnabled && user != null) {
            val docRef = firestore.collection("users")
                .document(user.uid)
                .collection("transactions")
                .document(transaction.id)

            try {
                if (transaction.isDeleted) {
                    docRef.delete().await()
                    // Sau khi xóa thành công trên Firebase, lưu vào hàng đợi xóa vĩnh viễn local
                    deletedItemDao.insert(DeletedItemEntity(transaction.id, DeletedItemType.TRANSACTION))
                } else {
                    docRef.set(transaction).await()
                }
            } catch (e: Exception) {
                // Log error or handle retry
            }
        } else if (transaction.isDeleted) {
            // Nếu không dùng sync, cho phép xóa vĩnh viễn luôn
            deletedItemDao.insert(DeletedItemEntity(transaction.id, DeletedItemType.TRANSACTION))
        }
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
