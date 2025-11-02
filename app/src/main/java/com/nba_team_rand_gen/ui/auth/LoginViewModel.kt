package com.nba_team_rand_gen.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.core.session.SessionManager
import com.nba_team_rand_gen.domain.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository,
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
}