package com.nba_team_rand_gen.data.model

data class MatchesUiState(
    val items: List<Match> = emptyList(),
    val loading: Boolean = true)
