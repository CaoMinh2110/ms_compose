package com.kkkk.moneysaving.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val budgetId: String?,
    val amount: Long,
    val note: String?,
    val occurredAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
)

