package com.kkkk.moneysaving.domain.model

data class Budget(
    val id: String = "",
    val name: String = "",
    val amount: Long = 0L,
    val color: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
)

