package com.example.moviematch.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.R
import com.example.moviematch.data.db.AppDatabase
import com.example.moviematch.data.repository.MoviePreferenceRepositoryImpl
import com.example.moviematch.presentation.factory.PreferencesViewModelFactory
import com.example.moviematch.presentation.viewmodel.PreferencesViewModel

/**
 * Composable screen that allows the user to manage their movie preferences.
 *
 * The user can select a genre and then choose movies from that genre to save as preferences.
 *
 * @param userId The ID of the currently logged-in user.
 * @param navController Navigation controller used for routing between screens.
 */
@Composable
fun PreferencesScreen(userId: String, navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as android.app.Application

    // Initialize repository and ViewModel
    val db = AppDatabase.getDatabase(context)
    val repo = remember { MoviePreferenceRepositoryImpl(db.moviePreferenceDao()) }
    val viewModel: PreferencesViewModel = viewModel(
        factory = PreferencesViewModelFactory(application, userId, repo)
    )

    // Collect UI state
    val genreMovieMap by viewModel.genreMovieMap.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()

    // Automatically select the first genre when data loads
    LaunchedEffect(genreMovieMap) {
        if (selectedGenre.isEmpty() && genreMovieMap.isNotEmpty()) {
            viewModel.selectedGenre.value = genreMovieMap.keys.minOf { it }
        }
    }

    val selectedByGenre by viewModel.selectedByGenre.collectAsState()
    val movies = genreMovieMap[selectedGenre]?.sorted() ?: emptyList()
    val selected = selectedByGenre[selectedGenre] ?: emptySet()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown menu for selecting movie genre
        GenreDropdownMenu(
            genres = genreMovieMap.keys.sorted(),
            selectedGenre = selectedGenre,
            onGenreSelected = { viewModel.selectedGenre.value = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // List of movies with checkboxes
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .width(350.dp)
        ) {
            items(movies) { movie ->
                val isChecked = selected.contains(movie)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.toggleMovieSelection(movie) }
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { viewModel.toggleMovieSelection(movie) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                            uncheckedColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                    Text(
                        text = movie,
                        modifier = Modifier.padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to save preferences
        Button(
            onClick = { viewModel.saveAll() },
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                stringResource(R.string.save_preferences),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to return to the main menu
        Button(
            onClick = { navController.navigate("menu/$userId") },
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                stringResource(R.string.back_to_menu),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Composable that displays a dropdown menu with a list of genres.
 *
 * @param genres List of genres to choose from.
 * @param selectedGenre Currently selected genre.
 * @param onGenreSelected Callback triggered when a genre is selected.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreDropdownMenu(
    genres: List<String>,
    selectedGenre: String,
    onGenreSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(350.dp)
    ) {
        // menuAnchor uses deprecated API temporarily until new one stabilizes
        @Suppress("DEPRECATION")
        TextField(
            readOnly = true,
            value = selectedGenre,
            onValueChange = {},
            label = { Text(stringResource(R.string.genre)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onSecondary,
                unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
                cursorColor = MaterialTheme.colorScheme.primary,
            )
        )

        // Actual dropdown content
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genres.forEach { genre ->
                DropdownMenuItem(
                    text = { Text(genre) },
                    onClick = {
                        onGenreSelected(genre)
                        expanded = false
                    }
                )
            }
        }
    }
}
