package com.nba_team_rand_gen.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.nba_team_rand_gen.data.model.Post
import com.nba_team_rand_gen.domain.repo.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExplorePostsUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ExploreScreenViewModel @Inject constructor(
    private val repo: PostsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ExplorePostsUiState())
    val state = _state.asStateFlow()

    // Keep track of the last document internally (do not expose to UI)
    private var lastVisible: DocumentSnapshot? = null

    init {
        loadPosts(isInitialLoad = true)
    }

    fun refresh() {
        // Reset everything for a refresh
        lastVisible = null
        loadPosts(isInitialLoad = true)
    }

    fun loadNextPage() {
        // Prevent multiple calls if already loading or if end reached
        val s = _state.value
        if (s.isLoading || s.isLoadingMore || s.endReached) return

        loadPosts(isInitialLoad = false)
    }

    private fun loadPosts(isInitialLoad: Boolean) {
        viewModelScope.launch {
            _state.update {
                if (isInitialLoad) it.copy(isLoading = true, error = null)
                else it.copy(isLoadingMore = true, error = null)
            }

            try {
                // Fetch data from repository
                val (newPosts, newCursor) = repo.explorePosts(lastVisible)

                // Update cursor for next time
                lastVisible = newCursor

                _state.update { current ->
                    val updatedList = if (isInitialLoad) newPosts else current.posts + newPosts

                    current.copy(
                        posts = updatedList,
                        isLoading = false,
                        isLoadingMore = false,
                        // If we got fewer items than requested, we hit the end
                        endReached = newPosts.size < 10
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, isLoadingMore = false, error = e.message)
                }
            }
        }
    }
}