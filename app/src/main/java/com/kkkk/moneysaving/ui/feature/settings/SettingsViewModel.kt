package com.kkkk.moneysaving.ui.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.UserProfile
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.languageCode,
        settingsRepository.currencyCode,
        settingsRepository.isSyncEnabled,
        authRepository.isLoggedIn,
        authRepository.userProfile,
        _isLoading,
        _error
    ) { args: Array<Any?> ->
        SettingsUiState(
            languageCode = args[0] as String,
            currencyCode = args[1] as String,
            isSyncEnabled = args[2] as Boolean,
            isLoggedIn = args[3] as Boolean,
            userProfile = args[4] as? UserProfile,
            isLoading = args[5] as Boolean,
            error = args[6] as? String
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun loginWithGoogle(context: Context) {
        viewModelScope.launch {
            clearError()
            val result = authRepository.signInWithGoogle(context) { 
                _isLoading.value = true 
            }
            result.onFailure { e -> 
                _error.value = e.message 
                _isLoading.value = false
            }
            result.onSuccess {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch { 
            settingsRepository.setSyncEnabled(false)
            authRepository.signOut() 
        }
    }

    fun setSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSyncEnabled(enabled)
            if (enabled) {
                syncData()
            }
        }
    }

    fun syncData() {
        viewModelScope.launch {
            _isLoading.value = true
            // TODO: Implement actual sync logic between Local Room and Remote Firestore
            // This will involve comparing timestamps and merging data
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}

data class SettingsUiState(
    val languageCode: String = "en",
    val currencyCode: String = "USD",
    val isSyncEnabled: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
