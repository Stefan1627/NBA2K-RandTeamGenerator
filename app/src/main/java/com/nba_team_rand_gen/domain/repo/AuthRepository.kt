package com.nba_team_rand_gen.domain.repo

import android.annotation.SuppressLint
import com.nba_team_rand_gen.data.model.User
import kotlinx.coroutines.flow.Flow

@SuppressLint("RestrictedApi")
interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(fullName: String, email: String, password: String): Result<User>
    suspend fun signOut()
    suspend fun updateProfile(fullName: String, email: String)
    suspend fun reloadCurrentUser()
}