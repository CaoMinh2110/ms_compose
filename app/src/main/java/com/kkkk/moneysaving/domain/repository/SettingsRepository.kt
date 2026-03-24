package com.kkkk.moneysaving.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val languageCode: Flow<String>
    val currencyCode: Flow<String>
    val isSyncEnabled: Flow<Boolean>
    suspend fun setLanguageCode(code: String)
    suspend fun setCurrencyCode(code: String)
    suspend fun setSyncEnabled(enabled: Boolean)
}
