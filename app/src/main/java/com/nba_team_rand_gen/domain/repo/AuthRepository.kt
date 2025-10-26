package com.nba_team_rand_gen.domain.repo

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

@SuppressLint("RestrictedApi")
interface AuthRepository {
    val currentUser: Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(fullName: String, email: String, password: String): Result<FirebaseUser>
    suspend fun signOut()
}