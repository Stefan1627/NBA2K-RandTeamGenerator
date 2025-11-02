package com.nba_team_rand_gen.ui.screens.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.ui.screens.profile.ProfileUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EditProfileScreenViewModel (
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