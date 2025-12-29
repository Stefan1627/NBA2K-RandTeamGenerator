package com.nba_team_rand_gen.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nba_team_rand_gen.data.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRemoteDataSource @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: AuthDataSource
) {
    private fun postsCol() =
        db.collection("posts")

    suspend fun createPost(title: String, json: String) {
        val uid = auth.uidOrThrow()
        Log.d("POST_DEBUG", "Creating post as uid=$uid")

        val data = hashMapOf(
            "ownerId" to uid,
            "title" to title,
            "json" to json,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("posts").add(data).await()
    }

    fun explorePostsFlow(): Flow<List<Post>> = callbackFlow {
        val reg = postsCol()
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val posts = snap?.documents.orEmpty().map { doc ->
                    Post(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        json = doc.getString("json") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                trySend(posts).isSuccess
            }
        awaitClose { reg.remove() }
    }

    fun myPostsFlow(): Flow<List<Post>> = callbackFlow {
        val reg = postsCol()
            .whereEqualTo("ownerId", auth.uidOrThrow())
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    close(err)
                    return@addSnapshotListener
                }

                val posts = snap?.documents.orEmpty().map { doc ->
                    Post(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        json = doc.getString("json") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                trySend(posts).isSuccess
            }
        awaitClose { reg.remove() }
    }


    suspend fun deletePost(id: String) {
        postsCol().document(id).delete().await()
    }

    suspend fun updatePost(id: String, title: String, json: String) {
        val data = hashMapOf(
            "title" to title,
            "json" to json,
            "timestamp" to System.currentTimeMillis()
        )
        postsCol().document(id).set(data).await()
    }
}