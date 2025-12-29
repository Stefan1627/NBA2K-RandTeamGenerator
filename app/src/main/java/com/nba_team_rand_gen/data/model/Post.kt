package com.nba_team_rand_gen.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = "",
    val title: String = "",
    val json: String = "",
    val timestamp: Long = 0L
)