package com.kkkk.moneysaving.data.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.kkkk.moneysaving.domain.model.UserProfile
import com.kkkk.moneysaving.domain.repository.AuthRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.google.firebase.Timestamp

private const val TAG = "AuthRepository"
private const val WEB_CLIENT_ID = "545342756641-n7vle04jb9puipdbrr6ap2l7104d6v3q.apps.googleusercontent.com"

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser != null)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override val userProfile: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                val profile = UserProfile(
                    uid = user.uid,
                    name = user.displayName ?: "",
                    email = user.email ?: "",
                    avatar = user.photoUrl?.toString() ?: ""
                )
                trySend(profile).isSuccess
            } else {
                trySend(null).isSuccess
            }
        }
        firebaseAuth.addAuthStateListener(listener)

        firebaseAuth.currentUser?.let { user ->
            val profile = UserProfile(
                uid = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                avatar = user.photoUrl?.toString() ?: ""
            )
            trySend(profile).isSuccess
        }

        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(
        context: Context,
        onLoading: () -> Unit
    ): Result<UserProfile> = withContext(Dispatchers.Main) {
        try {
            Log.d(TAG, "Starting Google Sign-In process")
            
            val activity = context.findActivity() ?: return@withContext Result.failure(
                Exception("Valid Activity context is required for Google Sign-In")
            )

            val credentialManager = CredentialManager.create(activity)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Requesting credentials via CredentialManager")
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            if (credential is CustomCredential && 
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                onLoading() // Trigger loading only after user selects an account
                Log.d(TAG, "Google ID Token received, signing in to Firebase")
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                val authResult = withContext(Dispatchers.IO) {
                    firebaseAuth.signInWithCredential(firebaseCredential).await()
                }
                
                val firebaseUser = authResult.user!!
                val uid = firebaseUser.uid
                
                val userDocRef = firestore.collection("users").document(uid)
                val userDoc = withContext(Dispatchers.IO) { userDocRef.get().await() }
                
                val profile = if (!userDoc.exists()) {
                    Log.d(TAG, "New user detected, creating Firestore document")
                    val newProfile = UserProfile(
                        uid = uid,
                        name = firebaseUser.displayName ?: "",
                        email = firebaseUser.email ?: "",
                        avatar = firebaseUser.photoUrl?.toString() ?: ""
                    )
                    
                    val userData = hashMapOf(
                        "email" to newProfile.email,
                        "name" to newProfile.name,
                        "avatar" to newProfile.avatar,
                        "createdAt" to Timestamp.now()
                    )
                    
                    withContext(Dispatchers.IO) { userDocRef.set(userData).await() }
                    newProfile
                } else {
                    Log.d(TAG, "Existing user logged in, retrieving data from Firestore")
                    UserProfile(
                        uid = uid,
                        name = userDoc.getString("name") ?: firebaseUser.displayName ?: "",
                        email = userDoc.getString("email") ?: firebaseUser.email ?: "",
                        avatar = userDoc.getString("avatar") ?: firebaseUser.photoUrl?.toString() ?: ""
                    )
                }

                Log.d(TAG, "Firebase sign-in successful: ${profile.email}")
                Result.success(profile)
            } else {
                Log.e(TAG, "Received invalid credential type: ${credential.type}")
                Result.failure(Exception("Invalid Google credential received"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in error", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    private fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }
}