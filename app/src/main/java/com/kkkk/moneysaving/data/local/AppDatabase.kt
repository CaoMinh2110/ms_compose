package com.kkkk.moneysaving.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kkkk.moneysaving.data.local.dao.BudgetDao
import com.kkkk.moneysaving.data.local.dao.DeletedItemDao
import com.kkkk.moneysaving.data.local.dao.TransactionDao
import com.kkkk.moneysaving.data.local.entity.BudgetEntity
import com.kkkk.moneysaving.data.local.entity.DeletedItemEntity
import com.kkkk.moneysaving.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        DeletedItemEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun deletedItemDao(): DeletedItemDao
}
