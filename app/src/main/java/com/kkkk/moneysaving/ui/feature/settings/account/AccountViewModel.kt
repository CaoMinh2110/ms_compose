package com.kkkk.moneysaving.ui.feature.settings.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.model.UserProfile
import com.kkkk.moneysaving.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {
    val uiState: StateFlow<AccountUiState> = authRepository.userProfile.map { user ->
        AccountUiState(userProfile = user!!)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AccountUiState()
    )
}

data class AccountUiState(
    val userProfile: UserProfile = UserProfile()
)
