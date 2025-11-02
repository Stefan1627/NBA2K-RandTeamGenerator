package com.nba_team_rand_gen.data.mappers

import com.google.firebase.auth.FirebaseUser
import com.nba_team_rand_gen.data.model.User

internal fun FirebaseUser.toData(): User =
    User (
        id = uid,
        name = displayName,
        email = email,
        photoUrl = photoUrl?.toString()
    )