package com.kkkk.moneysaving.domain.repository

import android.content.Context
import com.kkkk.moneysaving.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isLoggedIn: Flow<Boolean>
    val userProfile: Flow<UserProfile?>
    suspend fun signInWithGoogle(
        context: Context,
        onLoading: () -> Unit = {}
    ): Result<UserProfile>
    suspend fun signOut()
}
