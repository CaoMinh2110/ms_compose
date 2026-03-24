package com.kkkk.moneysaving.domain.model

import androidx.annotation.DrawableRes

data class Category(
    val id: String,
    val name: String,
    val type: CategoryType,
    val color: Long,
    @param:DrawableRes val icon: Int
)

