package com.nba_team_rand_gen.data.repo

import com.nba_team_rand_gen.domain.repo.AuthRepository
import com.nba_team_rand_gen.data.firebase.AuthDataSource
import kotlinx.coroutines.flow.Flow
import com.nba_team_rand_gen.data.model.User
import com.nba_team_rand_gen.data.mappers.toData
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Repository facade over AuthDataSource.
 * Converts Firebase callbacks into suspend/Flow APIs for ViewModels. */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: AuthDataSource
) : AuthRepository {
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

    override suspend fun updateProfile(
        fullName: String,
        email: String
    ) {
        auth.updateProfile(
            fullName = fullName.ifBlank { null },
            email = email.ifBlank { null },
        )
    }

    override suspend fun reloadCurrentUser() {
        auth.reloadCurrentUser()
    }
}