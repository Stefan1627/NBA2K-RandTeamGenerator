package com.example.nba_team_rand_gen

import android.content.Context
import kotlin.random.Random
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Team(
    val id: Int,
    val team_name: String,
    val type: String
)

@Serializable
data class Player(
    val id: Int,
    val player_name: String,
    val ovr: Int,
    val team_id: Int
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

    fun Randomize (type: String, game_type: String): Int {
        val json = Json { ignoreUnknownKeys = true }
        val numOptions = 2

        val teamsText = loadTeamsJson()
        val allTeams: List<Team> = json.decodeFromString(teamsText)
        val selectedTeams = when (type) {
            "All" -> allTeams
            "Current" -> allTeams.filter { it.type == "current" }
            "Classic" -> allTeams.filter { it.type == "classic" }
            else -> {
                println("Invalid type")
                return -1
            }
        }
        val validTeamIds = selectedTeams.map { it.id }.toSet()
        val teamNames = selectedTeams.associate { it.id to it.team_name }

        var nrPlayers = 0
        when (game_type) {
            "1vs1" -> nrPlayers = 2
            "2vs2" -> nrPlayers = 4
            "3vs3" -> nrPlayers = 6
            "4vs4" -> nrPlayers = 8
            "5vs5" -> nrPlayers = 10
            else -> {
                println("Invalid game_type")
                return -1
            }
        }

        val playersText = loadPlayersJson()
        val allPlayers: List<Player> = json.decodeFromString(playersText)

        val pool = allPlayers.filter { it.team_id in validTeamIds }
        if (pool.size < nrPlayers) {
            println("Not enough players (${pool.size}) for a team of size $nrPlayers in mode $type")
            return -1
        }

        repeat(numOptions) { opt ->
            val team = pickRandomTeam(pool, nrPlayers)
            println("Option ${opt + 1} ($type):")
            for (p in team) {
                val tm = teamNames[p.team_id] ?: "Unknown"
                println("  • ${p.player_name} (OVR ${p.ovr}) — $tm")
            }
            println()
        }

        return 1
    }
}