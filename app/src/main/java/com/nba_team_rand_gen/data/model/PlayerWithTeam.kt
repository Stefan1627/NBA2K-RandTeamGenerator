package com.nba_team_rand_gen.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerWithTeam(
    val player: Player,
    val teamName: String
)
