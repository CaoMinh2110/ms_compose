package com.kkkk.moneysaving.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_items")
data class DeletedItemEntity(
    @PrimaryKey val id: String,
    val type: DeletedItemType
)

enum class DeletedItemType {
    TRANSACTION,
    BUDGET
}
