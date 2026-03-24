package com.kkkk.moneysaving.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val HasCompletedOnboarding = booleanPreferencesKey("has_completed_onboarding")
    val LanguageCode = stringPreferencesKey("language_code")
    val CurrencyCode = stringPreferencesKey("currency_code")
    val IsSyncEnabled = booleanPreferencesKey("is_sync_enabled")
}
