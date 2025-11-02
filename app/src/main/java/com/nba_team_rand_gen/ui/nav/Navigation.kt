package com.nba_team_rand_gen.ui.nav

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.nba_team_rand_gen.ui.screens.favorites.FavoritesScreen
import com.nba_team_rand_gen.ui.screens.history.HistoryScreen
import com.nba_team_rand_gen.ui.screens.home.HomeScreen
import com.nba_team_rand_gen.ui.screens.profile.ProfileScreen
import com.nba_team_rand_gen.ui.screens.showplayer.ShowPlayerScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.nba_team_rand_gen.R

@Composable
fun Navigation(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnHomeRoot = currentRoute == HOME_ROOT

    BackHandler(enabled = isOnHomeRoot) {
        if(backPressedOnce) {
            (context as? Activity)?.moveTaskToBack(true)
        } else {
            backPressedOnce = true
            Toast.makeText(context, R.string.exit_retry, Toast.LENGTH_SHORT).show()
            scope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }

    NavHost(navController, startDestination = NavigationItem.Home.route) {
        // HOME (tab graph)
        navigation(
            route = NavigationItem.Home.route,
            startDestination = HOME_ROOT
        ) {
            composable(HOME_ROOT) {
                HomeScreen(
                    onNavigateShowRoute = { route -> navController.navigate(route) }
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
                ShowPlayerScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.popBackStack(
                            route = HOME_ROOT,
                            inclusive = false)
                    }
                )
            }
        }

        // FAVORITES (tab graph)
        navigation(
            route = NavigationItem.Favorites.route,
            startDestination = FAVORITES_ROOT
        ) {
            composable(FAVORITES_ROOT) { FavoritesScreen() }
        }

        // EXPLORE (tab graph)
        navigation(
            route = NavigationItem.Explore.route,
            startDestination = EXPLORE_ROOT
        ) {
            composable(EXPLORE_ROOT) {
                SimpleTabBody("Explore")
            }
        }

        // POST (tab graph)
        navigation(
            route = NavigationItem.Post.route,
            startDestination = POST_ROOT
        ) {
            composable(POST_ROOT) {
                SimpleTabBody("Post")
            }
        }

        // PROFILE (tab graph + child screens)
        navigation(
            route = NavigationItem.Profile.route,
            startDestination = PROFILE_ROOT
        ) {
            composable(PROFILE_ROOT) {
                ProfileScreen(
                    onHistoryRoute = { route -> navController.navigate(route) },
                    onMyPostsRoute = { route -> navController.navigate(route) },
                    onEditProfileRoute = { route -> navController.navigate(route) }
                )
            }

            composable("historyScreen") { HistoryScreen() }
            composable("myPostsScreen") { SimpleTabBody("MyPosts") }
            composable("editProfile")   { SimpleTabBody("editProfiles") }
        }
    }
}

@Composable
private fun SimpleTabBody(label: String) {
    Text(text = label, color = Color.White, modifier = Modifier.padding(16.dp))
}