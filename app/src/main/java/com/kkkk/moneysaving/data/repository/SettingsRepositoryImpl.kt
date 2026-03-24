package com.kkkk.moneysaving.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kkkk.moneysaving.data.datastore.PreferenceKeys
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override val languageCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.LanguageCode] ?: "en"
    }

    override val currencyCode: Flow<String> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.CurrencyCode] ?: "USD"
    }

    override val isSyncEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.IsSyncEnabled] ?: false
    }

    override suspend fun setLanguageCode(code: String) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.LanguageCode] = code
        }
    }

    override suspend fun setCurrencyCode(code: String) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.CurrencyCode] = code
        }
    }

    override suspend fun setSyncEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.IsSyncEnabled] = enabled
        }
    }
}
