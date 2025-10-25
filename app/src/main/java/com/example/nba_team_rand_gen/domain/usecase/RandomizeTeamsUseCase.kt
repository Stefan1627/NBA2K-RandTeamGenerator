package com.example.nba_team_rand_gen.domain.usecase

import android.app.Application
import com.example.nba_team_rand_gen.RandomizeGame
import com.example.nba_team_rand_gen.data.model.PlayerWithTeam

class RandomizeTeamsUseCase(
    private val app: Application
) {
    operator fun invoke(type: String, game: String): List<PlayerWithTeam> {
        return RandomizeGame(app).randomize(type, game)
    }
}