package com.kkkk.moneysaving.ui.feature.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import com.kkkk.moneysaving.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appStartRepository: AppStartRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(context: Context, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(context) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            result.onSuccess {
                appStartRepository.setCompletedOnboarding(true)
                _uiState.update { it.copy(isLoading = false) }
                onFinished(true)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun skip(onFinished: (Boolean) -> Unit) {
        onFinished(false)
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

