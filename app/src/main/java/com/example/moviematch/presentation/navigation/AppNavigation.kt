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

@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    AppNavGraph(
        navController = navController,
        startDestination = "main",
        isDarkTheme = isDarkTheme,
        onLanguageChange = onLanguageChange,
        onThemeToggle = onThemeToggle
    )
    //val navController = rememberNavController()
    //AppNavGraph(navController = navController, startDestination = "main")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    isDarkTheme: Boolean,
    onLanguageChange: (String) -> Unit,
    onThemeToggle: () -> Unit
) {
    //val mainViewModel: com.example.moviematch.presentation.viewmodel.MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("main") {
            //MainScreen(navController = navController)
            MainScreen(
                navController = navController,
                isDarkTheme = isDarkTheme,
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
            EditProfileScreen(userId = userId, navController = navController, onThemeToggle = onThemeToggle, onLanguageChange = onLanguageChange)
        }

        composable(NavRoute.Contacts.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            ContactsScreen(userId = userId, navController = navController)
        }
    }
}
