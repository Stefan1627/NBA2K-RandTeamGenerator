package com.nba_team_rand_gen.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val loading: Boolean = true
)

/** User profile VM (read-only for this task). Observes auth user
 * and profile fields. EditProfile is intentionally ignored. */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    repo: AuthRepository
): ViewModel() {

    val state = repo.currentUser
        .map { user ->
            if (user == null) ProfileUiState(loading = false)
            else ProfileUiState(
                displayName = user.name.orEmpty(),
                email = user.email.orEmpty(),
                photoUrl = user.photoUrl,
                loading = false
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())
}