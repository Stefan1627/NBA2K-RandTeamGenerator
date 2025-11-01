package com.nba_team_rand_gen.data.repo

import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.data.firebase.AuthDataSource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/** Repository facade over AuthDataSource.
 * Converts Firebase callbacks into suspend/Flow APIs for ViewModels. */
class AuthRepositoryImpl(
    private val auth: AuthDataSource = AuthDataSource()
) : AuthRepository {
    // Schimba tipul din interfata din User (firestore.auth.User) in FirebaseUser
    override val currentUser: Flow<FirebaseUser?> = auth.currentUser
    override suspend fun signIn(
        email: String,
        password: String
    ): Result<FirebaseUser> = runCatching { auth.signInEmail(email, password) }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String
    ): Result<FirebaseUser> = runCatching { auth.signUpEmail(fullName, email, password) }

    override suspend fun signOut() { auth.signOut() }
}