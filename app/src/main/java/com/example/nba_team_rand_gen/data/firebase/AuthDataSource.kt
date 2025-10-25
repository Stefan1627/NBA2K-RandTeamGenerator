package com.example.nba_team_rand_gen.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun uidOrThrow(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    fun signOut() = auth.signOut()
}