package com.example.nba_team_rand_gen.ui.screens.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nba_team_rand_gen.domain.usecase.RandomizeTeamsUseCase

class HomeViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(HomeViewModel::class.java))
        return HomeViewModel(
            randomize = RandomizeTeamsUseCase(app)
        ) as T
    }
}