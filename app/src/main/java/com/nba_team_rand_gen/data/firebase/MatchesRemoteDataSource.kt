package com.nba_team_rand_gen.data.firebase

import android.util.Log
import com.nba_team_rand_gen.data.model.Match
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MatchesRemoteDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: AuthDataSource = AuthDataSource()
) {
    private fun col() =
        db.collection("users")
            .document(auth.uidOrThrow())
            .collection("matches")

    suspend fun createMatch(name: String, json: String) {
        val data = hashMapOf(
            "name" to name,
            "json" to json,
            "favorite" to false,
            "timestamp" to System.currentTimeMillis()
        )
        col().add(data).await()
    }

    fun historyFlow(): Flow<List<Match>> = callbackFlow {
        val reg = col()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snap?.documents.orEmpty().map { d ->
                    Match(
                        id = d.id,
                        name = d.getString("name") ?: "",
                        json = d.getString("json") ?: "",
                        favorite = d.getBoolean("favorite") ?: false,
                        timestamp = d.getLong("timestamp") ?: 0L
                    )
                }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    fun favoritesFlow(): Flow<List<Match>> = callbackFlow {
        val reg = col()
            .whereEqualTo("favorite", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    // 🔎 Log it so you see the “create index” link in Logcat
                    Log.e("FavoritesRepo", "favoritesFlow error", err)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents.orEmpty().map { d ->
                    Match(
                        id = d.id,
                        name = d.getString("name") ?: "",
                        json = d.getString("json") ?: "",
                        favorite = d.getBoolean("favorite") ?: false,
                        timestamp = d.getLong("timestamp") ?: 0L
                    )
                }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    suspend fun toggleFavorite(id: String) {
        val ref = col().document(id)
        val current = ref.get().await()
        val fav = current.getBoolean("favorite") ?: false
        ref.update("favorite", !fav).await()
    }

    suspend fun deleteMatch(id: String) {
        col().document(id).delete().await()
    }
}