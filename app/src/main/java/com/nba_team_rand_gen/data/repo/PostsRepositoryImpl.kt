package com.nba_team_rand_gen.data.repo

import com.nba_team_rand_gen.data.firebase.PostsRemoteDataSource
import com.nba_team_rand_gen.data.model.Post
import com.nba_team_rand_gen.domain.repo.PostsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepositoryImpl @Inject constructor(
    private val remote: PostsRemoteDataSource
): PostsRepository {
    override fun posts(): Flow<List<Post>> = remote.postsFlow()

    override suspend fun createPost(title: String, json: String) {
        remote.createPost(title, json)
    }

    override suspend fun deletePost(postId: String) {
        remote.deletePost(postId)
    }

    override suspend fun updatePost(
        postId: String,
        title: String,
        json: String
    ) {
        remote.updatePost(postId, title, json)
    }
}