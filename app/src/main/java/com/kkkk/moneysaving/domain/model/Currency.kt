package com.kkkk.moneysaving.domain.model

import androidx.annotation.DrawableRes

data class Currency(
    val code: String,
    val displayName: String,
    val symbol: String,
    @param:DrawableRes val iconResId: Int,
)

