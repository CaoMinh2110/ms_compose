package com.kkkk.moneysaving.domain.model

data class Transaction(
    val id: String,
    val categoryId: String,
    val budgetId: String?,
    val amount: Long,
    val note: String?,
    val occurredAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
)

