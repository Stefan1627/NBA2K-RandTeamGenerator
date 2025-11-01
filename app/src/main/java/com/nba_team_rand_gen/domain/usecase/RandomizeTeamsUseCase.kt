package com.nba_team_rand_gen.domain.usecase

import com.nba_team_rand_gen.data.model.Player
import com.nba_team_rand_gen.data.model.PlayerWithTeam
import com.nba_team_rand_gen.data.repo.RosterRepository
import kotlin.random.Random

/** Business logic to generate random teams from the selected teamIds.
 * Uses Fisher–Yates shuffle to pick a uniform random subset of size
 * derived from mode (e.g., 5vs5 => 10 players). Returns PlayerWithTeam for UI. */
class RandomizeTeamsUseCase(
    private val repo: RosterRepository
) {
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

    suspend operator fun invoke(type: String, game: String): List<PlayerWithTeam> {
        val teams = repo.getTeams(type)
        val validIds = teams.map { it.id }.toSet()
        val teamNames = teams.associate { it.id to it.teamName }

        val needed = when (game) {
            "1vs1" -> 2
            "2vs2" -> 4
            "3vs3" -> 6
            "4vs4" -> 8
            "5vs5" -> 10
            else -> { return emptyList() }
        }

        val pool = repo.getPlayersForTeams(validIds)
        if (pool.size < needed) return emptyList()

        return pickRandomTeam(pool, needed).map { p ->
            PlayerWithTeam(p, teamNames[p.teamId] ?: "Unknown")
        }
    }
}