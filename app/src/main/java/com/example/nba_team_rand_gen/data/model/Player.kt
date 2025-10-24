package com.example.nba_team_rand_gen.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: Int,
    @SerialName("player_name")
    val playerName: String,
    val ovr: Int,
    @SerialName("team_id")
    val teamId: Int
)
