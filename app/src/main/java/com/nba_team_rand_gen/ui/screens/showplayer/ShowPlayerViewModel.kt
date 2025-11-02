package com.nba_team_rand_gen.ui.screens.showplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.data.model.PlayerWithTeam
import com.nba_team_rand_gen.domain.repo.MatchesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.collections.emptyList

data class ShowPlayerUiState(
    val teams: List<PlayerWithTeam> = emptyList(),
    val matchName: String = "",
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

/** State holder for the currently shown random players.
 * Also exposes actions to save to history/favorites. */
@HiltViewModel
class ShowPlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: MatchesRepository
) : ViewModel() {

    private val json: String = savedStateHandle.get<String>("teamsJson") ?: "[]"

    private val parsed: List<PlayerWithTeam> = runCatching {
        Json.decodeFromString<List<PlayerWithTeam>>(json)
    }.getOrDefault(emptyList())

    private val _state = MutableStateFlow(ShowPlayerUiState(teams = parsed))
    val state: StateFlow<ShowPlayerUiState> = _state

    fun updateName(v: String) = _state.update { it.copy(matchName = v) }

    fun save() = viewModelScope.launch {
        _state.update { it.copy(saving = true, error = null) }
        try {
            repo.saveMatch(_state.value.matchName.trim(), _state.value.teams)
            _state.update { it.copy(saving = false, saved = true) }
        } catch (t: Throwable) {
            _state.update { it.copy(saving = false, error = t.message) }
        }
    }
}