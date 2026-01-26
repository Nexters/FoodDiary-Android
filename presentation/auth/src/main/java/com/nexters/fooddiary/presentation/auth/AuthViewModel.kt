package com.nexters.fooddiary.presentation.auth

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.MavericksState
import com.nexters.fooddiary.core.common.auth.GoogleSignInAccount
import com.nexters.fooddiary.core.common.auth.GoogleSignInIntentProvider
import com.nexters.fooddiary.core.common.auth.getSignInErrorMessage
import com.nexters.fooddiary.core.common.auth.getWebClientId
import com.nexters.fooddiary.core.common.resource.ResourceProvider
import com.nexters.fooddiary.domain.usecase.DeleteAccountUseCase
import com.nexters.fooddiary.domain.usecase.GetCurrentUserUseCase
import com.nexters.fooddiary.domain.usecase.SignInWithGoogleUseCase
import com.nexters.fooddiary.domain.usecase.SignOutUseCase
import com.nexters.fooddiary.presentation.auth.R as AuthR
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isAuthenticated: Boolean? = null,
    val isLoading: Boolean = false,
    val signInError: String? = null,
    val signInIntent: Intent? = null
) : MavericksState

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val googleSignInIntentProvider: GoogleSignInIntentProvider,
    @ApplicationContext private val context: Context,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            checkAuthenticationStatus()
        }
    }

    private fun setState(update: AuthUiState.() -> AuthUiState) {
        _state.value = _state.value.update()
    }

    private suspend fun checkAuthenticationStatus() {
        val user = getCurrentUserUseCase()
        setState { copy(isAuthenticated = user != null) }
    }

    fun startGoogleSignIn(activityContext: Context) {
        val webClientId = activityContext.getWebClientId()
        if (webClientId.isNotEmpty()) {
            setState { copy(isLoading = true) }
            val intent = googleSignInIntentProvider.getSignInIntent(activityContext, webClientId)
            setState { copy(signInIntent = intent) }
        } else {
            setState {
                copy(signInError = resourceProvider.getString(AuthR.string.error_web_client_id_not_set))
            }
        }
    }

    fun consumeSignInIntent() {
        setState { copy(signInIntent = null) }
    }

    fun processSignInResult(activityContext: Context, resultIntent: Intent) {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = true, signInError = null) }

                val signInResult = googleSignInIntentProvider.getSignInResultFromIntent(activityContext, resultIntent)

                signInResult.onSuccess { account: GoogleSignInAccount ->
                    handleSignInSuccess(account)
                }.onFailure { exception ->
                    handleSignInFailure(exception)
                }
            } finally {
                setState { copy(isLoading = false) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = true) }
                signOutUseCase()
                checkAuthenticationStatus()
                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            try {
                setState { copy(isLoading = true, signInError = null) }

                val result = deleteAccountUseCase()
                result.onSuccess {
                    checkAuthenticationStatus()
                    setState { copy(isLoading = false) }
                }.onFailure { exception ->
                    setState {
                        copy(
                            isLoading = false,
                            signInError = exception.message ?: resourceProvider.getString(AuthR.string.error_delete_account_failed)
                        )
                    }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
            }
        }
    }

    private suspend fun handleSignInSuccess(account: GoogleSignInAccount) {
        val result = signInWithGoogleUseCase(account.idToken)
        result.onSuccess { user ->
            setState { copy(isAuthenticated = true) }
        }.onFailure { exception ->
            setState {
                copy(signInError = exception.message ?: resourceProvider.getString(AuthR.string.error_sign_in_failed))
            }
        }
    }

    private fun handleSignInFailure(exception: Throwable) {
        exception.getSignInErrorMessage()?.let { errorMessage ->
            setState { copy(signInError = errorMessage) }
        }
    }

    fun consumeSignInError() {
        setState { copy(signInError = null) }
    }
}
