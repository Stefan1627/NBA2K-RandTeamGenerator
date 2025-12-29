package com.nba_team_rand_gen.domain.repo

import com.nba_team_rand_gen.data.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    fun posts(): Flow<List<Post>>
    suspend fun createPost(title: String, json: String)
    suspend fun deletePost(postId: String)
    suspend fun updatePost(postId: String, title: String, json: String)
}