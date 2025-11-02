package com.nba_team_rand_gen.ui.screens.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nba_team_rand_gen.domain.repo.AuthRepository

class EditProfileScreenFactory(
    private val repo : AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileScreenViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown viewModel class: ${modelClass.name}")
    }

}