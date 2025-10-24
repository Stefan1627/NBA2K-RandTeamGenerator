package com.example.nba_team_rand_gen.data.repo

import com.example.nba_team_rand_gen.domain.repo.AuthRepository
import com.example.nba_team_rand_gen.data.firebase.AuthDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val auth: AuthDataSource = AuthDataSource()
) : AuthRepository {
    // Schimba tipul din interfata din User (firestore.auth.User) in FirebaseUser
    override val currentUser: Flow<FirebaseUser?> = auth.currentUser
    override suspend fun signOut() { auth.signOut() }
}