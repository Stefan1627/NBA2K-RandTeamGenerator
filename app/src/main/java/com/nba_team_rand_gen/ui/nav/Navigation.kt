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
import com.nba_team_rand_gen.ui.screens.favorites.FavoritesScreen
import com.nba_team_rand_gen.ui.screens.history.HistoryScreen
import com.nba_team_rand_gen.ui.screens.home.HomeScreen
import com.nba_team_rand_gen.ui.screens.profile.ProfileScreen
import com.nba_team_rand_gen.ui.screens.showplayer.ShowPlayerScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.nba_team_rand_gen.R
import com.nba_team_rand_gen.ui.screens.edit_profile.EditProfileScreen
import com.nba_team_rand_gen.ui.screens.posts.PostsScreen

@Composable
fun Navigation(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backPressedOnce by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isOnHomeRoot = currentRoute == Routes.HOME

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

    NavHost(navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(
                onNavigateShowRoute = { route -> navController.navigate(route) }
            )
        }

        composable(Routes.FAVORITES) { FavoritesScreen() }

        composable(Routes.HISTORY) { HistoryScreen() }

        composable(
            route = Routes.POST + "?title={title}&json={json}",
            arguments = listOf(
                navArgument("title") {
                    type = StringType
                    defaultValue = ""
                },
                navArgument("json") {
                    type = StringType
                    defaultValue = ""
                }
            )
        ) {
            PostsScreen(
                onDone = { navController.popBackStack() }
            )
        }

        composable(Routes.EXPLORE) { SimpleTabBody("Explore") }

        composable(Routes.EDIT_PROFILE) { EditProfileScreen(
            navController = navController
        ) }

        composable(Routes.PROFILE) { ProfileScreen(
                onHistoryRoute = { route -> navController.navigate(Routes.HISTORY) },
                onMyPostsRoute = { route -> navController.navigate(Routes.POST) },
                onEditProfileRoute = { route -> navController.navigate(Routes.EDIT_PROFILE) }
            )
        }

        composable(
            route = Routes.SHOW_PLAYER,
            arguments = listOf(
                navArgument("teamsJson") {
                    type = StringType
                    nullable = false
                }
            )
        ) { ShowPlayerScreen(
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack(
                        route = Routes.HOME,
                        inclusive = false
                    )
                }
            )
        }
    }
}

@Composable
private fun SimpleTabBody(label: String) {
    Text(text = label, color = Color.White, modifier = Modifier.padding(16.dp))
}