package com.kkkk.moneysaving.data.local.dao

import androidx.room.*
import com.kkkk.moneysaving.data.local.entity.DeletedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeletedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DeletedItemEntity)

    @Query("SELECT * FROM deleted_items")
    fun getAllDeletedItems(): Flow<List<DeletedItemEntity>>

    @Query("SELECT * FROM deleted_items")
    suspend fun getDeletedItemsSync(): List<DeletedItemEntity>

    @Delete
    suspend fun delete(item: DeletedItemEntity)

    @Query("DELETE FROM deleted_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
