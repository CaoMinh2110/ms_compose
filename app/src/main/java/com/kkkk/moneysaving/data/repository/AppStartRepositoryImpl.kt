package com.kkkk.moneysaving.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.kkkk.moneysaving.data.datastore.PreferenceKeys
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppStartRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppStartRepository {
    override val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[PreferenceKeys.HasCompletedOnboarding] ?: false
    }

    override suspend fun setCompletedOnboarding(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.HasCompletedOnboarding] = completed
        }
    }
}

