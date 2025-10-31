package com.nba_team_rand_gen.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nba_team_rand_gen.data.firebase.MatchesRemoteDataSource
import com.nba_team_rand_gen.data.repo.AuthRepositoryImpl
import com.nba_team_rand_gen.data.repo.MatchesRepositoryImpl
import com.nba_team_rand_gen.ui.screens.favorites.FavoritesFactory
import com.nba_team_rand_gen.ui.screens.favorites.FavoritesScreen
import com.nba_team_rand_gen.ui.screens.favorites.FavoritesViewModel
import com.nba_team_rand_gen.ui.screens.history.HistoryFactory
import com.nba_team_rand_gen.ui.screens.history.HistoryScreen
import com.nba_team_rand_gen.ui.screens.history.HistoryViewModel
import com.nba_team_rand_gen.ui.screens.home.HomeScreen
import com.nba_team_rand_gen.ui.screens.profile.ProfileScreen
import com.nba_team_rand_gen.ui.screens.profile.ProfileViewModel
import com.nba_team_rand_gen.ui.screens.profile.ProfileViewModelFactory
import com.nba_team_rand_gen.ui.screens.showplayer.ShowPlayerFactory
import com.nba_team_rand_gen.ui.screens.showplayer.ShowPlayerScreen
import com.nba_team_rand_gen.ui.screens.showplayer.ShowPlayerViewModel

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) {
            HomeScreen(
                onNavigateShowRoute = { route -> navController.navigate(route) }
            )
        }
        composable(NavigationItem.Favorites.route) { backStackEntry ->
            val remote = remember(backStackEntry) { MatchesRemoteDataSource() }
            val repo   = remember(remote) { MatchesRepositoryImpl(remote) }
            val vm: FavoritesViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = FavoritesFactory(repo)
            )

            FavoritesScreen(vm = vm)
        }
        composable(NavigationItem.Explore.route) { backStackEntry ->
            val remote = remember(backStackEntry) { MatchesRemoteDataSource() }
            val repo   = remember(remote) { MatchesRepositoryImpl(remote) }
            val vm: HistoryViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = HistoryFactory(repo)
            )

            HistoryScreen(vm = vm)
        }
        composable(NavigationItem.Post.route)  { SimpleTabBody("Post") }
        composable(NavigationItem.Profile.route){ backStackEntry ->
            val vm: ProfileViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ProfileViewModelFactory(AuthRepositoryImpl())
            )
            ProfileScreen(
                vm = vm,
                onHistoryClick = { navController.navigate(NavigationItem.Explore.route) },
                onMyPostsClick = { navController.navigate(NavigationItem.Post.route) },
                onEditProfileClick = { /* TODO: navigate to EditProfile when available */ }
            )
        }

        composable(
            route = "showPlayer?teamsJson={teamsJson}",
            arguments = listOf(
                navArgument("teamsJson") {
                    type = StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val remote = remember(backStackEntry) { MatchesRemoteDataSource() }
            val repo   = remember(remote) { MatchesRepositoryImpl(remote) }
            val vm: ShowPlayerViewModel = viewModel(
                viewModelStoreOwner = backStackEntry,
                factory = ShowPlayerFactory(repo)
            )

            ShowPlayerScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack(
                        route = NavigationItem.Home.route,
                        inclusive = false
                    )
                },
                vm = vm
            )
        }
    }
}

@Composable
private fun SimpleTabBody(label: String) {
    Text(text = label, color = Color.White, modifier = Modifier.padding(16.dp))
}