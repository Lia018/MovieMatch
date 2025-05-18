package com.example.moviematch.presentation.navigation

/**
 * Represents all navigable routes (screens) within the MovieMatch app.
 *
 * Each route may define its own route pattern and optional helper methods for route creation.
 * This sealed class ensures type-safe navigation and centralized route management.
 *
 * @property route The raw route string used by the navigation graph.
 */
sealed class NavRoute(val route: String) {

    /**
     * Route to the login screen.
     */
    data object Login : NavRoute("login")

    /**
     * Route to the registration screen.
     */
    data object Register : NavRoute("register")

    /**
     * Route to the menu screen that requires a user ID.
     */
    data object Menu : NavRoute("menu/{userId}") {
        /**
         * Generates a valid route string with the given userId.
         *
         * @param userId The ID of the user.
         * @return The formatted route string.
         */
        fun createRoute(userId: String) = "menu/$userId"
    }

    /**
     * Route to the match screen for the specified user.
     */
    data object Match : NavRoute("match/{userId}")

    /**
     * Route to the preferences screen for the specified user.
     */
    data object Preferences : NavRoute("preferences/{userId}")

    /**
     * Route to the edit profile screen for the specified user.
     */
    data object EditProfile : NavRoute("edit_profile/{userId}")

    /**
     * Route to the contacts screen for the specified user.
     */
    data object Contacts : NavRoute("contacts/{userId}")
}
