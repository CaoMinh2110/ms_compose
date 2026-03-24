package com.kkkk.moneysaving.domain.model

import androidx.annotation.DrawableRes

data class Language(
    val code: String,
    val displayName: String,
    @param:DrawableRes val iconResId: Int,
)

