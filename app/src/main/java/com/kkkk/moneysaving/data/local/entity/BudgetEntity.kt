package com.kkkk.moneysaving.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val amount: Long,
    val color: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
)

