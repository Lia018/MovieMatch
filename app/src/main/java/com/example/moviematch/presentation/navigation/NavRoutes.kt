package com.example.moviematch.presentation.navigation

sealed class NavRoute(val route: String) {
    data object Login : NavRoute("login")
    data object Register : NavRoute("register")
    data object Menu : NavRoute("menu/{userId}") {
        fun createRoute(userId: String) = "menu/$userId"
    }
    data object Match : NavRoute("match/{userId}")
    data object Preferences : NavRoute("preferences/{userId}")
    data object EditProfile : NavRoute("edit_profile/{userId}")
    data object Contacts : NavRoute("contacts/{userId}")
}
