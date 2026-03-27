package com.kkkk.moneysaving.ui.feature.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.kkkk.moneysaving.domain.model.Budget
import com.kkkk.moneysaving.domain.model.Transaction
import com.kkkk.moneysaving.domain.model.UserProfile
import com.kkkk.moneysaving.domain.repository.AuthRepository
import com.kkkk.moneysaving.domain.repository.BudgetRepository
import com.kkkk.moneysaving.domain.repository.SettingsRepository
import com.kkkk.moneysaving.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _showSyncConfirmDialog = MutableStateFlow(false)
    private val _showSyncSuccessDialog = MutableStateFlow(false)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.languageCode,
        settingsRepository.currencyCode,
        settingsRepository.isSyncEnabled,
        authRepository.isLoggedIn,
        authRepository.userProfile,
        _showSyncConfirmDialog,
        _showSyncSuccessDialog,
        _isLoading,
        _error
    ) { args: Array<Any?> ->
        SettingsUiState(
            languageCode = args[0] as String,
            currencyCode = args[1] as String,
            isSyncEnabled = args[2] as Boolean,
            isLoggedIn = args[3] as Boolean,
            userProfile = args[4] as? UserProfile,
            showSyncConfirmDialog = args[5] as Boolean,
            showSyncSuccessDialog = args[6] as Boolean,
            isLoading = args[7] as Boolean,
            error = args[8] as? String
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
            if (enabled) {
                val user = authRepository.userProfile.first()
                if (user != null) {
                    try {
                        _isLoading.value = true
                        if (isDataChanged(user.uid)) {
                            _showSyncConfirmDialog.value = true
                        } else {
                            settingsRepository.setSyncEnabled(true)
                            syncData()
                        }
                    } catch (_: Exception) {
                        settingsRepository.setSyncEnabled(true)
                        syncData()
                    } finally {
                        _isLoading.value = false
                    }
                }
            } else {
                settingsRepository.setSyncEnabled(false)
            }
        }
    }

    private suspend fun isDataChanged(uid: String): Boolean {
        val localTransactions = transactionRepository.observeAll().first()
        val localBudgets = budgetRepository.observeAll().first()

        val remoteTransactions = firestore.collection("users")
            .document(uid)
            .collection("transactions")
            .get()
            .await()
            .toObjects(Transaction::class.java)

        val remoteBudgets = firestore.collection("users")
            .document(uid)
            .collection("budgets")
            .get()
            .await()
            .toObjects(Budget::class.java)

        if (localTransactions.size != remoteTransactions.size || localBudgets.size != remoteBudgets.size) {
            return true
        }

        val localTxMap = localTransactions.associateBy { it.id }
        val allTxMatch = remoteTransactions.all { remote ->
            val local = localTxMap[remote.id]
            local != null && local.updatedAt == remote.updatedAt
        }
        if (!allTxMatch) return true

        val localBudgetMap = localBudgets.associateBy { it.id }
        val allBudgetsMatch = remoteBudgets.all { remote ->
            val local = localBudgetMap[remote.id]
            local != null && local.updatedAt == remote.updatedAt
        }
        return !allBudgetsMatch
    }

    fun confirmSync() {
        viewModelScope.launch {
            _showSyncConfirmDialog.value = false
            settingsRepository.setSyncEnabled(true)
            syncData()
        }
    }

    fun dismissSyncConfirmDialog() {
        _showSyncConfirmDialog.value = false
    }

    fun dismissSyncSuccessDialog() {
        _showSyncSuccessDialog.value = false
    }

    fun syncData() {
        viewModelScope.launch {
            val user = authRepository.userProfile.first() ?: return@launch
            _isLoading.value = true
            try {
                performFullSync(
                    uid = user.uid,
                    firestore = firestore,
                    transactionRepository = transactionRepository,
                    budgetRepository = budgetRepository
                )
                _showSyncSuccessDialog.value = true
            } catch (e: Exception) {
                _error.value = "Sync failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        suspend fun performFullSync(
            uid: String,
            firestore: FirebaseFirestore,
            transactionRepository: TransactionRepository,
            budgetRepository: BudgetRepository
        ) {
            val batch = firestore.batch()

            // 1. Sync Budgets trước để có danh sách ID hợp lệ cho Transactions
            val localBudgets = budgetRepository.observeAll().first()
            val remoteBudgets = firestore.collection("users").document(uid)
                .collection("budgets").get().await().toObjects(Budget::class.java)

            val budgetResults = resolveSync(localBudgets, remoteBudgets) {
                when (it) {
                    is Budget -> it.id to it.updatedAt
                    else -> "" to 0L
                }
            }

            budgetResults.toRemoteUpsert.forEach {
                batch.set(
                    firestore.collection("users").document(uid).collection("budgets")
                        .document((it as Budget).id), it
                )
            }
            budgetResults.toRemoteDeleteIds.forEach {
                batch.delete(
                    firestore.collection("users").document(uid).collection("budgets").document(it)
                )
            }

            // Cập nhật local budgets ngay để Transaction có thể tham chiếu
            if (budgetResults.toLocalUpsert.isNotEmpty()) {
                budgetRepository.upsertAll(budgetResults.toLocalUpsert)
            }

            // Lấy danh sách budget IDs cuối cùng (sau khi đã gộp local và remote sắp tới)
            val allValidBudgetIds =
                (localBudgets.map { it.id } + budgetResults.toLocalUpsert.map { (it as Budget).id }).toSet()

            // 2. Sync Transactions
            val localTxs = transactionRepository.observeAll().first()
            val remoteTxs = firestore.collection("users").document(uid)
                .collection("transactions").get().await().toObjects(Transaction::class.java)

            val txResults = resolveSync(localTxs, remoteTxs) {
                when (it) {
                    is Transaction -> it.id to it.updatedAt
                    else -> "" to 0L
                }
            }

            txResults.toRemoteUpsert.forEach {
                batch.set(
                    firestore.collection("users").document(uid).collection("transactions")
                        .document((it as Transaction).id), it
                )
            }
            txResults.toRemoteDeleteIds.forEach {
                batch.delete(
                    firestore.collection("users").document(uid).collection("transactions")
                        .document(it)
                )
            }

            // Xử lý logic budgetId mồ côi khi đưa vào local
            val cleanedLocalUpsert = txResults.toLocalUpsert.map { tx ->
                if (tx.budgetId != null && !allValidBudgetIds.contains(tx.budgetId)) {
                    tx.copy(budgetId = null)
                } else {
                    tx
                }
            }

            if (cleanedLocalUpsert.isNotEmpty()) {
                transactionRepository.upsertAll(cleanedLocalUpsert)
            }

            batch.commit().await()
        }

        private fun <T> resolveSync(
            local: List<T>,
            remote: List<T>,
            identity: (T) -> Pair<String, Long>
        ): SyncResult<T> {
            val toRemoteUpsert = mutableListOf<T>()
            val toRemoteDeleteIds = mutableListOf<String>()
            val toLocalUpsert = mutableListOf<T>()

            val remoteMap = remote.associateBy { identity(it).first }
            val localMap = local.associateBy { identity(it).first }

            local.forEach { l ->
                val id = identity(l).first
                val r = remoteMap[id]
                val isDeleted = when (l) {
                    is Transaction -> l.isDeleted
                    is Budget -> l.isDeleted
                    else -> false
                }

                if (r == null) {
                    if (isDeleted) toRemoteDeleteIds.add(id) else toRemoteUpsert.add(l)
                } else {
                    val lTime = identity(l).second
                    val rTime = identity(r).second
                    if (lTime > rTime) {
                        if (isDeleted) toRemoteDeleteIds.add(id) else toRemoteUpsert.add(l)
                    } else if (rTime > lTime) {
                        toLocalUpsert.add(r)
                    }
                }
            }

            remote.forEach { r ->
                val id = identity(r).first
                if (!localMap.containsKey(id)) {
                    toLocalUpsert.add(r)
                }
            }

            return SyncResult(toRemoteUpsert, toRemoteDeleteIds, toLocalUpsert)
        }

        private data class SyncResult<T>(
            val toRemoteUpsert: List<T>,
            val toRemoteDeleteIds: List<String>,
            val toLocalUpsert: List<T>
        )
    }
}

data class SettingsUiState(
    val languageCode: String = "en",
    val currencyCode: String = "USD",
    val isSyncEnabled: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userProfile: UserProfile? = null,
    val showSyncConfirmDialog: Boolean = false,
    val showSyncSuccessDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)
