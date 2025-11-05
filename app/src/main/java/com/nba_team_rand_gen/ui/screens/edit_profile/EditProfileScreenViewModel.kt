package com.nba_team_rand_gen.ui.screens.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.ui.nav.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val loading: Boolean = true,
    val error: String? = null,
    val navigate: EditProfileNav? = null
)


sealed interface EditProfileEvent {
    data class OnNameChanged(val value: String) : EditProfileEvent
    data class OnEmailChanged(val value: String) : EditProfileEvent
    data object OnSaveClick : EditProfileEvent
    data object OnBackClick : EditProfileEvent
    data object NavConsumed : EditProfileEvent
}

sealed interface EditProfileNav {
    data object Back : EditProfileNav
    data class To(val route: String) : EditProfileNav
}

private data class EditForm(
    val displayName: String? = null,
    val email: String? = null,
)

@HiltViewModel
class EditProfileScreenViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    private val reload = MutableStateFlow(0)
    private val _saving = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)


    @OptIn(ExperimentalCoroutinesApi::class)
    private val userState: Flow<EditProfileUiState> = reload.flatMapLatest {
        repo.currentUser
            .map { user ->
                if (user == null) {
                    EditProfileUiState(loading = false)
                } else {
                    EditProfileUiState(
                        displayName = user.name.orEmpty(),
                        email = user.email.orEmpty(),
                        loading = false
                    )
                }
            }
            .catch { e ->
                emit(
                    EditProfileUiState(
                        loading = false,
                        error = e.message ?: "Failed to load profile"
                    )
                )
            }
    }


    private val _form = MutableStateFlow(EditForm())
    private val _nav = MutableStateFlow<EditProfileNav?>(null)


    // UI colecteaza din aceasta stare combinata (user + form + nav)
    val state: StateFlow<EditProfileUiState> =
        combine(userState, _form, _nav) { base, form, nav ->
            base.copy(
                displayName = form.displayName ?: base.displayName,
                email = form.email ?: base.email,
                navigate = nav
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EditProfileUiState())


    fun refresh() {
        reload.update { it + 1 }
    }


    fun onEvent(e: EditProfileEvent) {
        when (e) {
            is EditProfileEvent.OnNameChanged ->
                _form.update { it.copy(displayName = e.value) }

            is EditProfileEvent.OnEmailChanged ->
                _form.update { it.copy(email = e.value) }

            EditProfileEvent.OnSaveClick -> {
                save()
            }

            EditProfileEvent.OnBackClick ->
                _nav.value = EditProfileNav.Back

            EditProfileEvent.NavConsumed -> {
                _nav.value = null
            }
        }
    }

    private fun save() {
        if (_saving.value) return
        val current = state.value
        val newName = _form.value.displayName ?: current.displayName
        val newEmail = _form.value.email ?: current.email

        viewModelScope.launch {
            _saving.value = true
            _error.value = null
            try {
                repo.updateProfile(newName, newEmail)
                repo.reloadCurrentUser()

                _nav.value = EditProfileNav.To(Routes.PROFILE)
            } catch (t: Throwable) {
                _error.value = t.message ?: "Save failed"
            } finally {
                _saving.value = false
            }
        }
    }
}