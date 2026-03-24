package com.kkkk.moneysaving.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppStartRepository {
    val hasCompletedOnboarding: Flow<Boolean>
    suspend fun setCompletedOnboarding(completed: Boolean)
}

