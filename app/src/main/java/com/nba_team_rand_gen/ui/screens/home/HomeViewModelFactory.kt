package com.nba_team_rand_gen.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nba_team_rand_gen.App
import com.nba_team_rand_gen.domain.usecase.RandomizeTeamsUseCase

class HomeViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(HomeViewModel::class.java))
        val repo = (app as App).container.rosterRepository
        return HomeViewModel(
            randomize = RandomizeTeamsUseCase(repo)
        ) as T
    }
}