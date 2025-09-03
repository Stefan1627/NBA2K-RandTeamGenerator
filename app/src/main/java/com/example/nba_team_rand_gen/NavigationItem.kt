package com.example.nba_team_rand_gen

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    data object Home : NavigationItem("home", R.drawable.favorite_svg, "Home")
    data object Music : NavigationItem("music", R.drawable.favorite_svg, "Music")
    data object Movies : NavigationItem("movies", R.drawable.favorite_svg, "Movies")
    data object Books : NavigationItem("books", R.drawable.favorite_svg, "Books")
    data object Profile : NavigationItem("profile", R.drawable.favorite_svg, "Profile")
}