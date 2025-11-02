package com.nba_team_rand_gen.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.core.session.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.nba_team_rand_gen.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/** Auth orchestration VM. Collects current user Flow,
 * exposes simple intents (signIn, signUp, signOut) and UI state for screens to observe.
 * All operations are cancellation-safe. */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val session: SessionManager
) : ViewModel() {

    val currentUser: StateFlow<User?> =
        repo.currentUser.stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null)

    val isLoggedIn: StateFlow<Boolean> =
        combine(currentUser, session.isValid) { user, valid ->
            user != null && valid
        }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            session.isValid.collect { valid ->
                if (!valid && currentUser.value != null) {
                    repo.signOut()
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            session.clear()
            repo.signOut()
        }
    }
}