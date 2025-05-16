package com.example.moviematch.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.R
import com.example.moviematch.presentation.viewmodel.MenuViewModel

@Composable
fun MenuScreen(
    userId: String,
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MovieImage()

        MenuButton(
            label = stringResource(R.string.edit_profile),
            onClick = { viewModel.navigateToEditProfile(navController, userId) }
        )
        MenuButton(
            label = stringResource(R.string.contacts),
            onClick = { viewModel.navigateToContacts(navController, userId) }
        )
        MenuButton(
            label = stringResource(R.string.edit_preferences),
            onClick = { viewModel.navigateToPreferences(navController, userId) }
        )
        MenuButton(
            label = stringResource(R.string.search_match),
            onClick = { viewModel.navigateToMatch(navController, userId) }
        )
        MenuButton(
            label = stringResource(R.string.logout),
            onClick = { viewModel.logout(navController) }
        )
    }
}

@Composable
fun MenuButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 8.dp)
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

@Composable
fun MovieImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "https://www.clker.com/cliparts/r/Q/s/z/K/W/lights-camera-action-hollywood-md.png",
            contentDescription = null,
            modifier = Modifier.width(250.dp)
        )
    }
}

