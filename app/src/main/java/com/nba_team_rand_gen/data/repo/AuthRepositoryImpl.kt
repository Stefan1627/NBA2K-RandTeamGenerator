package com.nba_team_rand_gen.data.repo

import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.data.firebase.AuthDataSource
import kotlinx.coroutines.flow.Flow
import com.nba_team_rand_gen.data.model.User
import com.nba_team_rand_gen.data.mappers.toData
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Repository facade over AuthDataSource.
 * Converts Firebase callbacks into suspend/Flow APIs for ViewModels. */
class AuthRepositoryImpl @Inject constructor(
    private val auth: AuthDataSource
) : AuthRepository {
    // Schimba tipul din interfata din User (firestore.auth.User) in FirebaseUser
    override val currentUser: Flow<User?> = auth.currentUser.map { it?.toData() }
    override suspend fun signIn(
        email: String,
        password: String
    ): Result<User> = runCatching { auth.signInEmail(email, password).toData() }

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: String
    ): Result<User> = runCatching { auth.signUpEmail(fullName, email, password).toData() }

    override suspend fun signOut() { auth.signOut() }
}