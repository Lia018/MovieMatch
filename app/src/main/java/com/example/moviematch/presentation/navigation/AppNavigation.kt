package com.example.moviematch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviematch.data.db.AppDatabase
import com.example.moviematch.data.repository.UserRepositoryImpl
import com.example.moviematch.presentation.factory.LoginViewModelFactory
import com.example.moviematch.presentation.factory.RegisterViewModelFactory
import com.example.moviematch.presentation.screen.ContactsScreen
import com.example.moviematch.presentation.screen.EditProfileScreen
import com.example.moviematch.presentation.screen.LoginScreen
import com.example.moviematch.presentation.screen.MainScreen
import com.example.moviematch.presentation.screen.MatchScreen
import com.example.moviematch.presentation.screen.MenuScreen
import com.example.moviematch.presentation.screen.PreferencesScreen
import com.example.moviematch.presentation.screen.RegisterScreen
import com.example.moviematch.presentation.viewmodel.LoginViewModel
import com.example.moviematch.presentation.viewmodel.RegisterViewModel

/**
 * Root composable for navigation.
 *
 * Initializes the NavController and sets up the navigation graph with optional handlers
 * for theme and language change events passed from the activity layer.
 *
 * @param isDarkTheme Boolean flag indicating if dark theme is active.
 * @param currentLanguage Language code currently active ("sk", "en", etc.).
 * @param onLanguageChange Callback invoked when the user changes the language.
 * @param onThemeToggle Callback invoked when the user toggles the theme.
 */
@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    AppNavGraph(
        navController = navController,
        startDestination = "main",
        isDarkTheme = isDarkTheme,
        currentLanguage = currentLanguage,
        onLanguageChange = onLanguageChange,
        onThemeToggle = onThemeToggle
    )
}

/**
 * Defines the navigation graph and routes for the app using Jetpack Navigation Compose.
 *
 * All app destinations (screens) are declared here using composable routes.
 *
 * @param navController Controller that manages navigation between composable.
 * @param startDestination Route name of the initial screen to be shown.
 * @param isDarkTheme Boolean flag indicating the current theme.
 * @param currentLanguage Language code currently active ("sk", "en", etc.).
 * @param onLanguageChange Callback invoked to update the app's language.
 * @param onThemeToggle Callback invoked to switch the app theme.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    isDarkTheme: Boolean,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable("main") {
            MainScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
                currentLanguage = currentLanguage,
                onLanguageChange = onLanguageChange,
                onThemeToggle = onThemeToggle
            )
        }

        composable(
            route = "${NavRoute.Login.route}?prefill={prefill}&fromRegister={fromRegister}",
            arguments = listOf(
                navArgument("prefill") { nullable = true },
                navArgument("fromRegister") { defaultValue = "false" }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val repo = remember { UserRepositoryImpl(db.userDao()) }
            val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(repo))

            val prefillId = backStackEntry.arguments?.getString("prefill")
            val fromRegistration = backStackEntry.arguments?.getString("fromRegister") == "true"

            LoginScreen(
                viewModel = viewModel,
                navController = navController,
                prefsName = "login_prefs",
                prefillUserId = prefillId,
                skipAutoLogin = fromRegistration
            )
        }

        composable(NavRoute.Register.route) {
            val context = LocalContext.current
            val db = remember { AppDatabase.getDatabase(context) }
            val repo = remember { UserRepositoryImpl(db.userDao()) }
            val viewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(repo))

            RegisterScreen(navController = navController, viewModel = viewModel)
        }

        composable(NavRoute.Menu.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            MenuScreen(userId = userId, navController = navController)
        }

        composable(NavRoute.Match.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            MatchScreen(userId = userId, navController = navController)
        }

        composable(NavRoute.Preferences.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            PreferencesScreen(userId = userId, navController = navController)
        }

        composable(NavRoute.EditProfile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            EditProfileScreen(
                userId = userId,
                navController = navController,
                currentLanguage = currentLanguage,
                onThemeToggle = onThemeToggle,
                onLanguageChange = onLanguageChange
            )
        }

        composable(NavRoute.Contacts.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            ContactsScreen(userId = userId, navController = navController)
        }
    }
}
