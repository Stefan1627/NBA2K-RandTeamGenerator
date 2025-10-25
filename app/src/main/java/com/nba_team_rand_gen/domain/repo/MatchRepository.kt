package com.nba_team_rand_gen.domain.repo

import com.nba_team_rand_gen.data.model.Match
import com.nba_team_rand_gen.data.model.PlayerWithTeam
import kotlinx.coroutines.flow.Flow

interface MatchesRepository {
    suspend fun saveMatch(name: String, teams: List<PlayerWithTeam>)
    fun favorites(): Flow<List<Match>>
    fun history(): Flow<List<Match>>
    suspend fun toggleFavorite(matchId: String)
    suspend fun deleteMatch(matchId: String)
}