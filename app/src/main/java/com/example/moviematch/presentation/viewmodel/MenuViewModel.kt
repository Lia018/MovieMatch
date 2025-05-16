package com.example.moviematch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController

class MenuViewModel : ViewModel() {

    fun navigateToEditProfile(navController: NavController, userId: String) {
        navController.navigate("edit_profile/$userId")
    }

    fun navigateToContacts(navController: NavController, userId: String) {
        navController.navigate("contacts/$userId")
    }

    fun navigateToPreferences(navController: NavController, userId: String) {
        navController.navigate("preferences/$userId")
    }

    fun navigateToMatch(navController: NavController, userId: String) {
        navController.navigate("match/$userId")
    }

    fun logout(navController: NavController) {
        navController.navigate("main") {
            popUpTo(0) { inclusive = true }
        }
    }
}
