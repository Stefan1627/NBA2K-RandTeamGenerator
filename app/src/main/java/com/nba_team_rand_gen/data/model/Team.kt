package com.nba_team_rand_gen.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val id: Int,
    @SerialName("team_name")
    val teamName: String = "Unknown",
    val type: String
)
