package com.kkkk.moneysaving.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kkkk.moneysaving.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY occurredAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE budgetId = :budgetId AND isDeleted = 0")
    fun observeByBudgetId(budgetId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND isDeleted = 0 ORDER BY occurredAt DESC")
    fun observeByCategoryId(categoryId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND occurredAt <= :endAt ORDER BY occurredAt DESC")
    fun observeByTimeRange(endAt: Long): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<TransactionEntity>)

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                "WHERE isDeleted = 0 " +
                "AND (budgetId = :budgetId OR :budgetId IS NULL) " +
                "AND (occurredAt <= :endAt OR :endAt IS NULL)",
    )
    fun sumAmountByBudgetAndRange(budgetId: String?, endAt: Long?): Flow<Long>

    @Update
    suspend fun update(entity: TransactionEntity)

    @Query("UPDATE transactions SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, updatedAt: Long)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query(
        "SELECT COALESCE(SUM(amount), 0) FROM transactions " +
                "WHERE isDeleted = 0 AND categoryId = :categoryId " +
                "AND occurredAt >= :startAt AND occurredAt <= :endAt"
    )
    fun sumAmountByCategoryAndRange(categoryId: String, startAt: Long, endAt: Long): Flow<Long>
}
