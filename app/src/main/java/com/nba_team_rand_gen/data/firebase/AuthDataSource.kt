package com.nba_team_rand_gen.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** FirebaseAuth wrapper. Exposes a cold Flow<FirebaseUser?> that emits auth state changes.
 *  Provides email/password sign-in & sign-up using suspendCancellableCoroutine, and signOut.
 *  UI subscribes to currentUser to reactively navigate when session changes. */
@Singleton
class AuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun uidOrThrow(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    /** Signs in with email/password and resumes with the FirebaseUser or throws on error. */
    suspend fun signInEmail(email: String, password: String): FirebaseUser =
        suspendCancellableCoroutine { cont ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { res -> cont.resume(res.user!!) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    /** Creates an account then updates displayName in the same chain;
     * resumes with the created FirebaseUser. */
    suspend fun signUpEmail(fullName: String, email: String, password: String): FirebaseUser =
        suspendCancellableCoroutine { cont ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { res ->
                    val user = res.user!!
                    val req = userProfileChangeRequest { displayName = fullName }
                    user.updateProfile(req)
                        .addOnSuccessListener { cont.resume(user) }
                        .addOnFailureListener { e -> cont.resumeWithException(e) }
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    fun signOut() = auth.signOut()
}