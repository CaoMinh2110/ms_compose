package com.kkkk.moneysaving.ui.navigate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import com.kkkk.moneysaving.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class StartupViewModel @Inject constructor(
    appStartRepository: AppStartRepository,
    authRepository: AuthRepository,
) : ViewModel() {
    val startDestination: StateFlow<String?> = combine(
        appStartRepository.hasCompletedOnboarding,
        authRepository.isLoggedIn,
    ) { hasCompleted, isLoggedIn ->
        when {
            !hasCompleted -> RootRoute.Intro
            isLoggedIn -> RootRoute.Main
            else -> RootRoute.Main
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}

