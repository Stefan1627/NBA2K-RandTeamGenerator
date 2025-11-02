package com.nba_team_rand_gen.data.repo

import com.nba_team_rand_gen.data.firebase.MatchesRemoteDataSource
import com.nba_team_rand_gen.data.model.Match
import com.nba_team_rand_gen.data.model.PlayerWithTeam
import com.nba_team_rand_gen.domain.repo.MatchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** Repository implementation combining remote Firestore
 * sources with small in-memory transforms.
 * Abstracts data layer away from ViewModels and UseCases. */
class MatchesRepositoryImpl @Inject constructor(
    private val remote: MatchesRemoteDataSource
) : MatchesRepository {

    override suspend fun saveMatch(name: String, teams: List<PlayerWithTeam>) {
        val json = Json.encodeToString(teams)
        remote.createMatch(name, json)
    }

    override fun favorites(): Flow<List<Match>> = remote.favoritesFlow()

    override fun history(): Flow<List<Match>> = remote.historyFlow()

    override suspend fun toggleFavorite(matchId: String) {
        remote.toggleFavorite(matchId)
    }

    override suspend fun deleteMatch(matchId: String) {
        remote.deleteMatch(matchId)
    }
}