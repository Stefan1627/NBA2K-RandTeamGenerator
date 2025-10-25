package com.nba_team_rand_gen.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.data.model.MatchesUiState
import com.nba_team_rand_gen.data.repo.MatchesRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repo: MatchesRepositoryImpl) : ViewModel() {
    val state = repo.favorites()
        .map { MatchesUiState(items = it, loading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MatchesUiState())

    fun onToggleFavorite(id: String) = viewModelScope.launch { repo.toggleFavorite(id) }
    fun onDelete(id: String) = viewModelScope.launch { repo.deleteMatch(id) }
}