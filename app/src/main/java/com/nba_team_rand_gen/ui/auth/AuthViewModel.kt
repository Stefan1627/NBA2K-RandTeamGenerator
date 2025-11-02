package com.nba_team_rand_gen.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nba_team_rand_gen.data.repo.AuthRepositoryImpl
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.google.firebase.auth.FirebaseUser
import com.nba_team_rand_gen.core.session.SessionManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.nba_team_rand_gen.data.model.User

/** Auth orchestration VM. Collects current user Flow,
 * exposes simple intents (signIn, signUp, signOut) and UI state for screens to observe.
 * All operations are cancellation-safe. */
class AuthViewModel(
    private val repo: AuthRepository = AuthRepositoryImpl(),
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

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as Application
                AuthViewModel(
                    repo = AuthRepositoryImpl(),
                    session = SessionManager.from(app))}
        }
    }
}