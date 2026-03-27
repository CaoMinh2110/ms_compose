package com.kkkk.moneysaving.domain.model

data class Transaction(
    val id: String = "",
    val categoryId: String = "",
    val budgetId: String? = null,
    val amount: Long = 0L,
    val note: String? = null,
    val occurredAt: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val isDeleted: Boolean = false,
)
