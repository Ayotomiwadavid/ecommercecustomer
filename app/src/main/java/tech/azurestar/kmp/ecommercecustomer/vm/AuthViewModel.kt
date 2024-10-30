package tech.azurestar.kmp.ecommercecustomer.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import tech.azurestar.kmp.ecommercecustomer.language.TextProvider

class AuthViewModel(client: SupabaseClient) : ViewModel() {

    private val auth = client.auth
    val currentUser = MutableStateFlow(auth.currentUserOrNull())
    val isInitialized = MutableStateFlow(false)

    init {

        viewModelScope.launch {

            auth.awaitInitialization()

            isInitialized.value = true

            currentUser.value = auth.currentUserOrNull()

            auth.sessionStatus.collect {
                when (it) {
                    is SessionStatus.Authenticated -> {
                        when (it.source) {
                            is SessionSource.SignIn -> currentUser.value = auth.currentUserOrNull()
                            is SessionSource.SignUp -> currentUser.value = auth.currentUserOrNull()
                            else -> {}
                        }
                    }
//                    SessionStatus.LoadingFromStorage -> currentUser.value = auth.retrieveUserForCurrentSession(true)
                    is SessionStatus.NotAuthenticated -> {
                        if (it.isSignOut) {
                            currentUser.value = null
                        }
                    }

                    else -> {}
                }
            }
        }

    }

    fun signInWithEmail(
        email: String,
        password: String,
        successful: () -> Unit,
        callback: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                successful()
            } catch (e: Exception) {
                throw e
                callback(e.message ?: TextProvider.AN_ERROR_OCCURRED.getText())
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, callback: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                callback(TextProvider.PLEASE_CHECK_YOUR_EMAIL.getText())
            } catch (e: Exception) {
                callback(e.message ?: TextProvider.AN_ERROR_OCCURRED.getText())
                throw e
            }
        }
    }

    fun signOut(callback: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                auth.signOut()
            } catch (e: Exception) {
                callback(e.message ?: TextProvider.AN_ERROR_OCCURRED.getText())
            }
        }
    }
}