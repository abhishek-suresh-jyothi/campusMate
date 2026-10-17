package com.example.presentation.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
        if (firebaseAuth.currentUser != null) {
            _isLoading.value = false
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Interactive Google Sign-In using Credential Manager and GetSignInWithGoogleOption.
     * Enforces single-option request exclusivity per the Credential Manager guidelines.
     */
    fun launchGoogleSignIn(activity: Activity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val credentialManager = CredentialManager.create(activity)
            val webClientId = try {
                activity.getString(R.string.default_web_client_id)
            } catch (e: Exception) {
                Log.e(TAG, "Missing default_web_client_id string resource", e)
                _errorMessage.value = "Google Client ID configuration missing. Please check Firebase setup."
                _isLoading.value = false
                return@launch
            }

            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activity
                )

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    authenticateWithFirebase(idToken)
                } else {
                    Log.w(TAG, "Unexpected credential type returned: ${credential::class.java.name}")
                    _errorMessage.value = "Received invalid credential format from Google Sign-In."
                    _isLoading.value = false
                }
            } catch (e: GetCredentialCancellationException) {
                // User cancelled the bottom sheet flow - expected user behavior
                Log.i(TAG, "Google Sign-In was cancelled by the user")
                _isLoading.value = false
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Google Sign-In failed via Credential Manager", e)
                _errorMessage.value = "Google Sign-In failed: ${e.localizedMessage ?: "Unknown error"}"
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during authentication", e)
                _errorMessage.value = e.localizedMessage ?: "Sign-in encountered an error."
                _isLoading.value = false
            }
        }
    }

    /**
     * Silent/Automatic Sign-In attempt using GetGoogleIdOption (separate request exclusivity).
     */
    fun attemptSilentSignIn(context: Context) {
        if (auth.currentUser != null) return

        viewModelScope.launch {
            try {
                val webClientId = context.getString(R.string.default_web_client_id)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(true)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context = context, request = request)

                val credential = result.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    authenticateWithFirebase(googleIdTokenCredential.idToken)
                }
            } catch (unused: Exception) {
                // Silent sign-in is best-effort; fall back to interactive sign-in UI
                Log.d(TAG, "Silent sign-in not available, awaiting user interaction")
            }
        }
    }

    private suspend fun authenticateWithFirebase(idToken: String) {
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            _currentUser.value = authResult.user
            _isLoading.value = false
            Log.d(TAG, "Successfully authenticated with Firebase: ${authResult.user?.uid}")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase signInWithCredential failed", e)
            _errorMessage.value = "Firebase Authentication failed: ${e.localizedMessage ?: "Unknown error"}"
            _isLoading.value = false
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
