package com.nba_team_rand_gen.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val loading: Boolean = true
)

/** User profile VM (read-only for this task). Observes auth user
 * and profile fields. EditProfile is intentionally ignored. */
class ProfileViewModel(
    repo: AuthRepository
): ViewModel() {

    val state = repo.currentUser
        .map { user ->
            if (user == null) ProfileUiState(loading = false)
            else ProfileUiState(
                displayName = user.displayName.orEmpty(),
                email = user.email.orEmpty(),
                photoUrl = user.photoUrl?.toString(),
                loading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())
}