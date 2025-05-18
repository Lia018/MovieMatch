package com.example.moviematch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

/**
 * ViewModel responsible for handling navigation from the main menu screen
 * to other parts of the application, such as editing the profile, managing contacts,
 * viewing preferences, searching for matches, or logging out.
 */
class MenuViewModel : ViewModel() {

    /**
     * Navigates to the Edit Profile screen for the specified user.
     *
     * @param navController The navigation controller used to perform navigation.
     * @param userId The unique identifier of the user.
     */
    fun navigateToEditProfile(navController: NavController, userId: String) {
        navController.navigate("edit_profile/$userId")
    }

    /**
     * Navigates to the Contacts screen for the specified user.
     *
     * @param navController The navigation controller used to perform navigation.
     * @param userId The unique identifier of the user.
     */
    fun navigateToContacts(navController: NavController, userId: String) {
        navController.navigate("contacts/$userId")
    }

    /**
     * Navigates to the Preferences screen for the specified user.
     *
     * @param navController The navigation controller used to perform navigation.
     * @param userId The unique identifier of the user.
     */
    fun navigateToPreferences(navController: NavController, userId: String) {
        navController.navigate("preferences/$userId")
    }

    /**
     * Navigates to the Match screen for the specified user,
     * where the user can search for potential movie matches.
     *
     * @param navController The navigation controller used to perform navigation.
     * @param userId The unique identifier of the user.
     */
    fun navigateToMatch(navController: NavController, userId: String) {
        navController.navigate("match/$userId")
    }

    /**
     * Logs out the user and navigates back to the Main screen.
     * Clears the back stack to prevent navigation back to protected screens.
     *
     * @param navController The navigation controller used to perform navigation.
     */
    fun logout(navController: NavController) {
        navController.navigate("main") {
            popUpTo(0) { inclusive = true }
        }
    }
}
