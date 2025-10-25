package com.nba_team_rand_gen.ui.screens.showplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.nba_team_rand_gen.domain.repo.MatchesRepository

class ShowPlayerFactory(
    private val repo: MatchesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()
        @Suppress("UNCHECKED_CAST")
        return ShowPlayerViewModel(handle, repo) as T
    }
}