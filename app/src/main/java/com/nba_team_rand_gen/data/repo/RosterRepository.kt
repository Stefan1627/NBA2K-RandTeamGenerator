package com.nba_team_rand_gen.data.repo

import android.app.Application
import kotlinx.serialization.json.Json
import com.nba_team_rand_gen.data.model.Team
import com.nba_team_rand_gen.data.model.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Roster data access (teams/players). Implemented elsewhere;
 * ViewModels/UseCases depend only on this contract. */
class RosterRepository(
    private val app: Application
) {
    private val json = Json{ignoreUnknownKeys = true}

    private val allTeams: List<Team> by lazy {
        app.assets.open("nba_teams.json").bufferedReader().use{
            json.decodeFromString<List<Team>>(it.readText())
        }
    }

    private val allPlayers: List<Player> by lazy {
        app.assets.open("players.json").bufferedReader().use {
            json.decodeFromString<List<Player>>(it.readText())
        }
    }

    suspend fun getTeams(type: String): List<Team> = withContext(Dispatchers.IO) {
        when (type) {
            "All" -> allTeams
            "All-time" -> allTeams.filter { it.type == "all-time"}
            "Current" -> allTeams.filter { it.type == "current" }
            "Classic" -> allTeams.filter { it.type == "classic" }
            else -> { emptyList() }
        }
    }

    suspend fun getPlayersForTeams(validTeamIds: Set<Int>):
            List<Player> = withContext(Dispatchers.IO) {
        allPlayers.filter { it.teamId in validTeamIds }
    }
}