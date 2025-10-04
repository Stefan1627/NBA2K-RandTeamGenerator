package com.example.nba_team_rand_gen

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    data object Home : NavigationItem("home", R.drawable.fav_sel_svg, "Home")
    data object Favorites : NavigationItem("favorites", R.drawable.fav_sel_svg, "Favorites")
    data object Explore : NavigationItem("explore", R.drawable.fav_sel_svg, "Explore")
    data object Post : NavigationItem("post", R.drawable.fav_sel_svg, "Post")
    data object Profile : NavigationItem("profile", R.drawable.fav_sel_svg, "Profile")
}