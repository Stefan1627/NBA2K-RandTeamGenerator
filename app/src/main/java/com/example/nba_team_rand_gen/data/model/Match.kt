package com.example.nba_team_rand_gen.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Match(
    val id: String = "",            // doc id in Firestore
    val name: String = "",
    val json: String = "",          // Json.encodeToString(List<PlayerWithTeam>)
    val favorite: Boolean = false,
    val timestamp: Long = 0         // pentru sortare (history)
)