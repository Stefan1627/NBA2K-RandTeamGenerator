package com.nba_team_rand_gen.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.data.model.MatchesUiState
import com.nba_team_rand_gen.domain.repo.MatchesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Manages favorite players/teams. Exposes Flow-based state sourced from repository. */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: MatchesRepository
) : ViewModel() {
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = refreshTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            repo.favorites()
                .map { MatchesUiState(items = it, loading = false) }
                .onStart { emit(MatchesUiState(loading = true)) }
                .catch {
                    e -> emit(MatchesUiState(loading = false,
                    error = e.message ?: "Unknown error"))
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MatchesUiState())

    fun onToggleFavorite(id: String) = viewModelScope.launch { repo.toggleFavorite(id) }
    fun onDelete(id: String) = viewModelScope.launch { repo.deleteMatch(id) }
    fun refresh() { refreshTrigger.tryEmit(Unit) }
}