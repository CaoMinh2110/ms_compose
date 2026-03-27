package com.kkkk.moneysaving.ui.feature.settings.account

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.UserProfile
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val imageRepository: ImageRepository,
) : ViewModel() {
    val uiState: StateFlow<AccountUiState> = authRepository.userProfile.map { user ->
        AccountUiState(userProfile = user!!)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AccountUiState()
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun updateUserProfile(name: String, avatarUri: String?) {
        viewModelScope.launch {
            _isLoading.update { true }
            _errorMessage.update { null }

            var avatarUrl: String? = null
            
            // Upload new image if provided
            if (!avatarUri.isNullOrEmpty()) {
                val uploadResult = imageRepository.uploadProfileImage(Uri.parse(avatarUri))
                uploadResult.onSuccess { url ->
                    avatarUrl = url
                }
                uploadResult.onFailure { error ->
                    _errorMessage.update { "Failed to upload image: ${error.message}" }
                    _isLoading.update { false }
                    return@launch
                }
            }

            // Update profile with new data
            val updateResult = authRepository.updateUserProfile(
                name = name,
                avatar = avatarUrl
            )

            updateResult.onSuccess {
                // Update will be reflected through userProfile flow
            }
            updateResult.onFailure { error ->
                _errorMessage.update { error.message ?: "Failed to update profile" }
            }

            _isLoading.update { false }
        }
    }
}

data class AccountUiState(
    val userProfile: UserProfile = UserProfile()
)
