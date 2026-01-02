package com.nba_team_rand_gen.ui.screens.posts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nba_team_rand_gen.domain.repo.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostsUiState(
    val title: String = "",
    val json: String = "",
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PostsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        PostsUiState(
            title = savedStateHandle.get<String>("title") ?: "",
            json = savedStateHandle.get<String>("json") ?: ""
        )
    )
    val state: StateFlow<PostsUiState> = _state

    fun onTitleChange(value: String) {
        _state.update { it.copy(title = value) }
    }

    fun onJsonChange(value: String) {
        _state.update { it.copy(json = value) }
    }

    fun savePost() = viewModelScope.launch {
        val s = _state.value
        if (s.title.isBlank()) {
            _state.update { it.copy(error = "Title cannot be empty.") }
            return@launch
        }

        _state.update { it.copy(saving = true, error = null) }

        try {
            postsRepository.createPost(
                title = s.title.trim(),
                json = s.json
            )

            _state.update { it.copy(saving = false, saved = true) }
        } catch (t: Throwable) {
            _state.update {
                it.copy(
                    saving = false,
                    error = t.message ?: "Failed to create post"
                )
            }
        }
    }
}