package com.kkkk.moneysaving.ui.feature.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.repository.AppStartRepository
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val appStartRepository: AppStartRepository,
    private val settingsRepository: SettingsRepository,
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val firestore: FirebaseFirestore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signInWithGoogle(context: Context, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(context) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            result.onSuccess { user ->
                // Bật sync
                settingsRepository.setSyncEnabled(true)

                // Pull dữ liệu từ Firebase
                val hasData = pullDataFromFirebase(user.uid)

                appStartRepository.setCompletedOnboarding(true)
                _uiState.update { it.copy(isLoading = false) }
                onFinished(hasData)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun pullDataFromFirebase(uid: String): Boolean {
        try {
            val budgetsSnapshot = firestore.collection("users")
                .document(uid)
                .collection("budgets")
                .get()
                .await()

            val allBudgets = budgetsSnapshot.documents.mapNotNull { doc ->
                try {
                    Budget(
                        id = doc.id,
                        name = doc.getString("name").orEmpty(),
                        amount = doc.getLong("amount") ?: 0L,
                        color = doc.getLong("color") ?: 0L,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                        updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                        isDeleted = doc.getBoolean("isDeleted") ?: false
                    )
                } catch (_: Exception) {
                    null
                }
            }
            val activeBudgetIdSet = allBudgets.filter { !it.isDeleted }.map { it.id }.toSet()

            if (allBudgets.isNotEmpty()) {
                budgetRepository.upsertAll(allBudgets)
            }
            val transactionsSnapshot = firestore.collection("users")
                .document(uid)
                .collection("transactions")
                .get()
                .await()

            val allTransactions = transactionsSnapshot.documents.mapNotNull { doc ->
                try {
                    val rawBudgetId = doc.getString("budgetId")

                    val budgetId =
                        if (rawBudgetId != null && activeBudgetIdSet.contains(rawBudgetId)) {
                            rawBudgetId
                        } else {
                            null
                        }

                    Transaction(
                        id = doc.id,
                        categoryId = doc.getString("categoryId").orEmpty(),
                        budgetId = budgetId,
                        amount = doc.getLong("amount") ?: 0L,
                        note = doc.getString("note"),
                        occurredAt = doc.getLong("occurredAt") ?: 0L,
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        updatedAt = doc.getLong("updatedAt") ?: 0L,
                        isDeleted = doc.getBoolean("isDeleted") ?: false
                    )
                } catch (_: Exception) {
                    null
                }
            }
            if (allTransactions.isNotEmpty()) {
                transactionRepository.upsertAll(allTransactions)
            }
            return allBudgets.isNotEmpty() || allTransactions.isNotEmpty()
        } catch (_: Exception) {
            // Log error
            return false
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
