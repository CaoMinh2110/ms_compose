package com.kkkk.moneysaving.ui.navigate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import com.kkkk.moneysaving.ui.feature.settings.SettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartupViewModel @Inject constructor(
    private val appStartRepository: AppStartRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {
    private val _loadingProgress = MutableStateFlow(0f)
    val loadingProgress: StateFlow<Float> = _loadingProgress
    val startDestination: StateFlow<RootRoute?> = combine(
        appStartRepository.hasCompletedOnboarding,
        authRepository.isLoggedIn,
    ) { hasCompleted, _ ->
        if (!hasCompleted) RootRoute.Intro else RootRoute.Main
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        syncDataFromRemote()
    }

    private fun syncDataFromRemote() {
        viewModelScope.launch {
            _loadingProgress.value = 0.1f
            val isLoggedIn = authRepository.isLoggedIn.first()
            val isSyncEnabled = settingsRepository.isSyncEnabled.first()

            if (isLoggedIn && isSyncEnabled) {
                val user = authRepository.userProfile.first()
                if (user != null) {
                    try {
                        _loadingProgress.value = 0.3f
                        SettingsViewModel.performFullSync(
                            uid = user.uid,
                            firestore = firestore,
                            transactionRepository = transactionRepository,
                            budgetRepository = budgetRepository
                        )
                        _loadingProgress.value = 0.8f
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            _loadingProgress.value = 1.0f
        }
    }
}
