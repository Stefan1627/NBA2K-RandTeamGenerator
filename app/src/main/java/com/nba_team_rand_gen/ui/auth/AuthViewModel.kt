package com.nba_team_rand_gen.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nba_team_rand_gen.data.repo.AuthRepositoryImpl
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> =
        repo.currentUser.stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null)

    fun signOut(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.signOut()
            onDone()
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { AuthViewModel(AuthRepositoryImpl()) }
        }
    }
}