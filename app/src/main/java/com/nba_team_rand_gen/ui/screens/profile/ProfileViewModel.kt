package com.nba_team_rand_gen.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val navigateToHistory: String? = null,
    val navigateToEditProfile: String? = null,
    val navigateToMyPosts: String? = null,
    val loading: Boolean = true
)

sealed interface ProfileEvent{
    data object OnHistoryClick: ProfileEvent
    data object OnEditProfileClick: ProfileEvent
    data object OnMyPostsClick: ProfileEvent
    data object NavConsumed : ProfileEvent
}

/** User profile VM (read-only for this task). Observes auth user
 * and profile fields. EditProfile is intentionally ignored. */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    repo: AuthRepository
) : ViewModel() {

    private val userState = repo.currentUser
        .map { user ->
            if (user == null) ProfileUiState(loading = false)
            else ProfileUiState(
                displayName = user.name.orEmpty(),
                email = user.email.orEmpty(),
                photoUrl = user.photoUrl,
                loading = false
            )
        }

    private val _nav = MutableStateFlow(ProfileUiState())

    // UI colecteaza din aceasta stare combinata (user + navigare)
    val state: StateFlow<ProfileUiState> = combine(userState, _nav) { u, n ->
        u.copy(
            navigateToHistory = n.navigateToHistory,
            navigateToEditProfile = n.navigateToEditProfile,
            navigateToMyPosts = n.navigateToMyPosts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun onEvent(e: ProfileEvent) {
        when (e) {
            ProfileEvent.OnHistoryClick ->
                _nav.update { it.copy(navigateToHistory = "historyScreen") }
            ProfileEvent.OnMyPostsClick ->
                _nav.update { it.copy(navigateToMyPosts = "myPostsScreen") }
            ProfileEvent.OnEditProfileClick ->
                _nav.update { it.copy(navigateToEditProfile = "editProfile") }
            ProfileEvent.NavConsumed ->
                _nav.update { it.copy(
                    navigateToHistory = null,
                    navigateToMyPosts = null,
                    navigateToEditProfile = null
                ) }
        }
    }
}
