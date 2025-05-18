package com.example.moviematch.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviematch.R
import com.example.moviematch.presentation.viewmodel.MenuViewModel

/**
 * Composable function displaying the main menu screen for the user.
 *
 * Provides navigation buttons to edit profile, contacts, preferences,
 * find a movie match, and logout from the app.
 *
 * @param userId The ID of the currently logged-in user.
 * @param navController Navigation controller used to move between screens.
 * @param viewModel ViewModel that handles navigation logic for the menu.
 */
@Composable
fun MenuScreen(
    userId: String,
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Displays the image at the top of the menu
        MovieImage()

        // Menu navigation buttons
        MenuButton(
            label = context.getString(R.string.edit_profile),
            onClick = { viewModel.navigateToEditProfile(navController, userId) }
        )
        MenuButton(
            label = context.getString(R.string.contacts),
            onClick = { viewModel.navigateToContacts(navController, userId) }
        )
        MenuButton(
            label = context.getString(R.string.edit_preferences),
            onClick = { viewModel.navigateToPreferences(navController, userId) }
        )
        MenuButton(
            label = context.getString(R.string.search_match),
            onClick = { viewModel.navigateToMatch(navController, userId) }
        )
        MenuButton(
            label = context.getString(R.string.logout),
            onClick = { viewModel.logout(navController) }
        )
    }
}

/**
 * Reusable composable button used in the main menu screen.
 *
 * @param label Text displayed on the button.
 * @param onClick Action to execute when the button is clicked.
 */
@Composable
fun MenuButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .width(350.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = 18.sp
        )
    }
}

/**
 * Composable that displays a themed image at the top of the MenuScreen.
 *
 * Currently loads a static image representing the movie theme.
 */
@Composable
fun MovieImage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "https://www.clker.com/cliparts/r/Q/s/z/K/W/lights-camera-action-hollywood-md.png",
            contentDescription = null,
            modifier = Modifier.height(200.dp)
        )
    }
}
