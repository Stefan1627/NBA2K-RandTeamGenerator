package com.nba_team_rand_gen.ui.nav

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


const val HOME_ROOT = "homeRoot"
const val FAVORITES_ROOT = "favoritesRoot"
const val EXPLORE_ROOT = "exploreRoot"
const val POST_ROOT = "postRoot"
const val PROFILE_ROOT = "profileRoot"

fun NavHostController.navigateBottomTab(
    tabRoute: String
) {
    navigate(tabRoute) {
        launchSingleTop = true
        restoreState = false
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }
}

fun rootForTab(tabRoute: String): String = when (tabRoute) {
    NavigationItem.Home.route      -> HOME_ROOT
    NavigationItem.Favorites.route -> FAVORITES_ROOT
    NavigationItem.Explore.route   -> EXPLORE_ROOT
    NavigationItem.Post.route      -> POST_ROOT
    NavigationItem.Profile.route   -> PROFILE_ROOT
    else -> tabRoute
}