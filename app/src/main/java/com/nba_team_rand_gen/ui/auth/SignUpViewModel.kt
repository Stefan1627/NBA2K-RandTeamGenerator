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

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

sealed interface SignUpEvent {
    data class FullNameChanged(val v: String) : SignUpEvent
    data class EmailChanged(val v: String) : SignUpEvent
    data class PasswordChanged(val v: String) : SignUpEvent
    data object Submit : SignUpEvent
    data object ErrorConsumed : SignUpEvent
    data object SuccessConsumed : SignUpEvent
}

/** Handles account creation. After success, displayName is set via Firebase profile update. */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val session: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state

    fun onEvent(e: SignUpEvent) {
        when (e) {
            is SignUpEvent.FullNameChanged -> _state.update { it.copy(fullName = e.v) }
            is SignUpEvent.EmailChanged    -> _state.update { it.copy(email = e.v) }
            is SignUpEvent.PasswordChanged -> _state.update { it.copy(password = e.v) }
            SignUpEvent.Submit -> submit()
            SignUpEvent.ErrorConsumed   -> _state.update { it.copy(error = null) }
            SignUpEvent.SuccessConsumed -> _state.update { it.copy(success = false) }
        }
    }

    private fun submit() {
        val name = _state.value.fullName.trim()
        val email = _state.value.email.trim()
        val pass  = _state.value.password
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _state.update { it.copy(error = "All fields required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val res = repo.signUp(name, email, pass)
            _state.update {
                it.copy(
                    isLoading = false,
                    error = res.exceptionOrNull()?.localizedMessage,
                    success = res.isSuccess
                )
            }
            if (res.isSuccess) {
                // Flip the shared session flow -> AuthViewModel.isLoggedIn becomes true
                session.start(1)
            }
        }
    }
}