package com.example.nba_team_rand_gen

import android.content.Context
import kotlin.random.Random
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Team(
    val id: Int,
    @SerialName("team_name")
    val teamName: String = "Unknown",
    val type: String
)

@Serializable
data class Player(
    val id: Int,
    @SerialName("player_name")
    val playerName: String,
    val ovr: Int,
    @SerialName("team_id")
    val teamId: Int
)

@Serializable
data class PlayerWithTeam(
    val player: Player,
    val teamName: String
)

class RandomizeGame(private val context: Context) {
    private fun loadTeamsJson(): String =
        context.assets
            .open("nba_teams.json")
            .bufferedReader()
            .use { it.readText() }

    private fun loadPlayersJson(): String =
        context.assets
            .open("players.json")
            .bufferedReader()
            .use { it.readText() }

    private fun <T> MutableList<T>.fisherYatesShuffle(random: Random = Random.Default) {
        for (i in lastIndex downTo 1) {
            val j = random.nextInt(i + 1)
            this[i] = this[j].also { this[j] = this[i] }
        }
    }

    private fun pickRandomTeam(pool: List<Player>, teamSize: Int): List<Player> {
        val copy = pool.toMutableList()
        copy.fisherYatesShuffle()
        return copy.take(teamSize)
    }

    fun randomize (type: String, gameType: String): List<PlayerWithTeam> {
        val json = Json { ignoreUnknownKeys = true }

        val teamsText = loadTeamsJson()
        val allTeams: List<Team> = json.decodeFromString(teamsText)
        val selectedTeams = when (type) {
            "All" -> allTeams
            "All-time" -> allTeams.filter { it.type == "all-time"}
            "Current" -> allTeams.filter { it.type == "current" }
            "Classic" -> allTeams.filter { it.type == "classic" }
            else -> {
                println("Invalid type")
                return emptyList()
            }
        }

        val validTeamIds = selectedTeams.map { it.id }.toSet()
        val teamNames = selectedTeams.associate { it.id to it.teamName }

        var nrPlayers = 0
        nrPlayers = when (gameType) {
            "1vs1" -> 2
            "2vs2" -> 4
            "3vs3" -> 6
            "4vs4" -> 8
            "5vs5" -> 10
            else -> {
                println("Invalid game_type")
                return emptyList()
            }
        }

        val playersText = loadPlayersJson()
        val allPlayers: List<Player> = json.decodeFromString(playersText)

        val pool = allPlayers.filter { it.teamId in validTeamIds }
        if (pool.size < nrPlayers) {
            println("Not enough players (${pool.size}) for a team of size $nrPlayers in mode $type")
            return emptyList()
        }

        return pickRandomTeam(pool, nrPlayers)
            .map { player ->
                PlayerWithTeam(player, teamNames[player.teamId] ?: "Unknown")
            }
    }
}