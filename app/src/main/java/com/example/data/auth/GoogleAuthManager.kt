package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.local.entities.UserEntity
import com.example.data.models.UserRole
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

data class GoogleSignInResult(
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val idToken: String?,
    val firebaseUid: String?
)

class GoogleAuthManager(
    private val context: Context,
    val firebaseConfigManager: FirebaseConfigManager = FirebaseConfigManager(context)
) {

    private val credentialManager = CredentialManager.create(context)
    private val TAG = "GoogleAuthManager"

    private fun getWebClientId(): String {
        return firebaseConfigManager.getEffectiveWebClientId()
    }

    suspend fun signInWithGoogle(activityContext: Activity): Result<GoogleSignInResult> {
        return try {
            val serverClientId = getWebClientId()
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

            val googleIdOption = try {
                GetSignInWithGoogleOption.Builder(serverClientId)
                    .setNonce(hashedNonce)
                    .build()
            } catch (e: Throwable) {
                try {
                    GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(serverClientId)
                        .setAutoSelectEnabled(false)
                        .setNonce(hashedNonce)
                        .build()
                } catch (e2: Throwable) {
                    return Result.failure(Exception("GOOGLE_CHOOSER_FALLBACK:Failed to build GoogleIdOption"))
                }
            }

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )
            handleCredentialResponse(result)
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In was cancelled by user")
            Result.failure(Exception("Sign-In cancelled by user."))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.type} - ${e.message}", e)
            Result.failure(Exception("GOOGLE_CHOOSER_FALLBACK:${e.localizedMessage ?: e.message}"))
        } catch (e: Throwable) {
            Log.e(TAG, "Sign-In unexpected error: ${e.message}", e)
            Result.failure(Exception("GOOGLE_CHOOSER_FALLBACK:${e.localizedMessage ?: e.message}"))
        }
    }

    private suspend fun handleCredentialResponse(result: GetCredentialResponse): Result<GoogleSignInResult> {
        val credential = result.credential
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val email = googleIdTokenCredential.id
                        val displayName = googleIdTokenCredential.displayName
                        val photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        val idToken = googleIdTokenCredential.idToken

                        var firebaseUid: String? = null
                        if (idToken.isNotBlank()) {
                            try {
                                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                                val authResult = FirebaseAuth.getInstance().signInWithCredential(authCredential).await()
                                firebaseUid = authResult.user?.uid
                            } catch (e: Exception) {
                                Log.w(TAG, "Firebase Auth with Google credential skipped or failed: ${e.message}")
                            }
                        }

                        return Result.success(
                            GoogleSignInResult(
                                email = email,
                                displayName = displayName,
                                photoUrl = photoUrl,
                                idToken = idToken,
                                firebaseUid = firebaseUid
                            )
                        )
                    } catch (e: GoogleIdTokenParsingException) {
                        return Result.failure(Exception("Received invalid Google ID Token response."))
                    }
                } else {
                    return Result.failure(Exception("Unexpected credential type received: ${credential.type}"))
                }
            }
            else -> {
                return Result.failure(Exception("Unsupported credential response."))
            }
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseAuth sign out error: ${e.message}")
            }
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }
    }
}
