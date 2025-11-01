package com.nba_team_rand_gen.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nba_team_rand_gen.core.session.SessionManager
import com.nba_team_rand_gen.data.repo.AuthRepositoryImpl
import com.nba_team_rand_gen.domain.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

sealed interface LoginEvent {
    data class EmailChanged(val v: String) : LoginEvent
    data class PasswordChanged(val v: String) : LoginEvent
    data object Submit : LoginEvent
    data object ErrorConsumed : LoginEvent
    data object SuccessConsumed : LoginEvent
}

/** Handles email/password sign-in. Validates input minimally and forwards to AuthRepository. */
class LoginViewModel(
    private val repo: AuthRepository = AuthRepositoryImpl(),
    private val session: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state

    fun onEvent(e: LoginEvent) {
        when (e) {
            is LoginEvent.EmailChanged -> _state.update { it.copy(email = e.v) }
            is LoginEvent.PasswordChanged -> _state.update { it.copy(password = e.v) }
            LoginEvent.Submit -> {
                val email = _state.value.email.trim()
                val password = _state.value.password
                if (email.isBlank() || password.isBlank()) {
                    _state.update { it.copy(error = "Please enter email and password") }
                    return
                }
                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true, error = null) }
                    val res = repo.signIn(email, password)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = res.exceptionOrNull()?.localizedMessage,
                            success = res.isSuccess
                        )
                    }
                    if (res.isSuccess) {
                        session.start(1)
                    }
                }
            }
            LoginEvent.ErrorConsumed -> _state.update { it.copy(error = null) }
            LoginEvent.SuccessConsumed -> _state.update { it.copy(success = false) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                LoginViewModel(
                    repo = AuthRepositoryImpl(),
                    session = SessionManager.from(app)
                ) }
        }
    }
}