package com.nba_team_rand_gen.domain.repo

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

@SuppressLint("RestrictedApi")
interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    suspend fun signOut()
}