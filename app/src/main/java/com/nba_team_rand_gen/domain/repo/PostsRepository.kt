package com.nba_team_rand_gen.domain.repo

import com.google.firebase.firestore.DocumentSnapshot
import com.nba_team_rand_gen.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    fun myPosts(): Flow<List<Post>>
    suspend fun explorePosts(lastVisible: DocumentSnapshot?): Pair<List<Post>, DocumentSnapshot?>
    suspend fun createPost(title: String, json: String)
    suspend fun deletePost(postId: String)
    suspend fun updatePost(postId: String, title: String, json: String)
}