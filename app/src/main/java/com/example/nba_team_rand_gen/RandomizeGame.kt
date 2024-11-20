package com.example.nba_team_rand_gen

import BackgroundWorker
import android.content.Context
import android.util.Log
import kotlin.random.Random

class RandomizeGame(private val context: Context) {
    fun Randomize (type: String, game_type: String): Int {
        var left = 0;
        var right = 0;
        var nrPlayers = 0;
        var player_id = 0
        when (type) {
            "All" -> right = 97
            "Current" -> right = 31
            "Classic" -> {
                left = 30
                right = 97
            }
            else -> {
                println("Invalid type")
                return -1
            }
        }

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
        val teamIds = List(nrPlayers) { Random.nextInt(left, right)}
        Log.d("RandomizeGame", "Team ids: $teamIds")
        val backgroundWorker = BackgroundWorker(context)

        for(x in teamIds) {
            backgroundWorker.executeNumTeams(x) {
                nrPlayers -> player_id = Random.nextInt(1, nrPlayers)
                Log.d("RandomizeGame", "Player: $player_id")
                backgroundWorker.executeExtractPLayer(1, 1)
            }
        }


        return 1
    }
}