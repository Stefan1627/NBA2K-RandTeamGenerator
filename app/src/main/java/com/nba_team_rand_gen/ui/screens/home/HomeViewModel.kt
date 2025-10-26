package com.nba_team_rand_gen.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.usecase.RandomizeTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class HomeUiState(
    val type: String = "All",
    val game: String = "1vs1",
    val navigateToShow: String? = null // json
)

sealed interface HomeEvent {
    data class OnType(val v: String): HomeEvent
    data class OnGame(val v: String): HomeEvent
    data object Randomize: HomeEvent
    data object NavConsumed: HomeEvent
}

class HomeViewModel(
    private val randomize: RandomizeTeamsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    fun onEvent(e: HomeEvent) {
        when (e) {
            is HomeEvent.OnType -> _state.update { it.copy(type = e.v) }
            is HomeEvent.OnGame -> _state.update { it.copy(game = e.v) }
            HomeEvent.Randomize -> {
                viewModelScope.launch {
                    val teams = randomize(_state.value.type, _state.value.game)
                    val json = Json.encodeToString(teams)
                    _state.update { it.copy(navigateToShow = json) }
                }
            }
            HomeEvent.NavConsumed -> _state.update { it.copy(navigateToShow = null) }
        }
    }
}
