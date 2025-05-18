package com.example.moviematch.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.moviematch.R
import com.example.moviematch.data.db.AppDatabase
import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.data.repository.ContactRepositoryImpl
import com.example.moviematch.data.repository.MoviePreferenceRepositoryImpl
import com.example.moviematch.data.repository.UserRepositoryImpl
import com.example.moviematch.presentation.factory.MatchViewModelFactory
import com.example.moviematch.presentation.navigation.NavRoute
import com.example.moviematch.presentation.viewmodel.MatchViewModel

/**
 * Main screen that allows a user to find movie matches with other users or contacts.
 *
 * @param userId ID of the current user
 * @param navController Navigation controller to handle screen transitions
 */
@Composable
fun MatchScreen(userId: String, navController: NavController) {
    val context = LocalContext.current

    // Initialize repositories and database access
    val db = AppDatabase.getDatabase(context)
    val movieRepo = remember { MoviePreferenceRepositoryImpl(db.moviePreferenceDao()) }
    val contactRepo = remember { ContactRepositoryImpl(db.contactDao()) }
    val userRepo = remember { UserRepositoryImpl(db.userDao()) }

    // Create the ViewModel using the factory
    val viewModel: MatchViewModel = viewModel(
        factory = MatchViewModelFactory(userId, movieRepo, contactRepo, userRepo)
    )

    val genres by viewModel.availableGenres.collectAsState()
    val allOptionLabel = stringResource(R.string.all_preferences)

    // Add "All" option at the beginning of the genre list
    val allGenresWithAllOption = remember(genres) {
        if (genres.isNotEmpty()) listOf(allOptionLabel) + genres else emptyList()
    }

    // Keep track of selected genres using Boolean flags
    val selectedStates = remember(allGenresWithAllOption) {
        mutableStateListOf(*BooleanArray(allGenresWithAllOption.size).toTypedArray())
    }

    val inputUserId by viewModel.inputUserId.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val matchResult by viewModel.matchResult.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showGenreDialog by rememberSaveable { mutableStateOf(false) }

    // Sync selected genres with stored preferences and update checkboxes
    LaunchedEffect(viewModel.selectedGenres.collectAsState().value, allGenresWithAllOption) {
        viewModel.loadGenres()
        viewModel.loadSelectedGenres(context)

        val selectedFromVM = viewModel.selectedGenres.value

        selectedStates.indices.forEach { index ->
            val genre = allGenresWithAllOption.getOrNull(index)
            selectedStates[index] = selectedFromVM.contains(genre)
        }

        // If all genres (excluding "All") are selected, mark "All" as selected too
        if (selectedStates.size > 1) {
            val allSelected = selectedStates.drop(1).all { it }
            selectedStates[0] = allSelected
        }
    }

    // Show messages sent via ViewModel (resource ID)
    LaunchedEffect(Unit) {
        viewModel.uiMessage.collect { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    // Show messages with arguments (e.g., formatted strings)
    LaunchedEffect(Unit) {
        viewModel.uiTextMessage.collect { (resId, args) ->
            val msg = context.getString(resId, *args.toTypedArray())
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Display match result dialogs (single or group match)
    matchResult?.let { (matchedUser, movies) ->
        if (matchedUser != null) {
            MatchResultDialog(
                matchedUserId = matchedUser,
                commonMovies = movies,
                contacts = contacts,
                onDismiss = { viewModel.resetMatchResult() },
                onAddToContacts = {
                    viewModel.addToContacts(matchedUser)
                    viewModel.resetMatchResult()
                }
            )
        } else {
            GroupMatchResultDialog(
                commonMovies = movies,
                onDismiss = { viewModel.resetMatchResult() }
            )
        }
    }

    // Main screen layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // User ID input field
        OutlinedTextField(
            value = inputUserId,
            onValueChange = { viewModel.inputUserId.value = it },
            label = { Text(stringResource(R.string.enter_user_id)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(350.dp),
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

        Spacer(modifier = Modifier.height(12.dp))

        // Button to find direct match
        Button(
            onClick = { viewModel.findDirectMatch(context) },
            modifier = Modifier.width(350.dp),
        ) {
            Text(stringResource(R.string.find_match))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button to initiate group match from contacts
        Button(
            onClick = {
                if (contacts.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.no_contacts_found), Toast.LENGTH_SHORT).show()
                } else {
                    showDialog = true
                }
            },
            modifier = Modifier.width(350.dp),
        ) {
            Text(stringResource(R.string.find_match_with_contact))
        }

        if (showDialog) {
            MultiContactSelectionDialog(
                allContacts = contacts,
                onConfirm = {
                    showDialog = false
                    viewModel.findGroupMatch(it.map { contact -> contact.contactId })
                },
                onDismiss = { showDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button to open genre selection dialog
        Button(
            onClick = { showGenreDialog = true },
            modifier = Modifier.width(350.dp),
        ) {
            Text(stringResource(R.string.genre_feeling_prompt))
        }

        if (showGenreDialog) {
            GenreSelectionDialog(
                allGenresWithAllOption = allGenresWithAllOption,
                selectedStates = selectedStates,
                allOptionLabel = allOptionLabel,
                onConfirm = { selected ->
                    viewModel.selectedGenres.value = selected
                    viewModel.saveSelectedGenres(context)
                    showGenreDialog = false
                    Toast.makeText(context, context.getString(R.string.genres_selected), Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showGenreDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Button to navigate back to the main menu
        Button(
            onClick = {
                navController.navigate(NavRoute.Menu.createRoute(userId))
            },
            modifier = Modifier.width(350.dp),
        ) {
            Text(stringResource(R.string.back_to_menu))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Display a movie-related image
        AnotherMovieImage()
    }
}

/**
 * Dialog showing the result of a direct match between the user and another matched user.
 *
 * Supports shake gesture to highlight a random common movie.
 *
 * @param matchedUserId ID of the matched user
 * @param commonMovies List of movies shared between users
 * @param contacts List of user's saved contacts
 * @param onDismiss Called when the user closes the dialog
 * @param onAddToContacts Called when the user chooses to add the matched user to contacts
 */
@Composable
fun MatchResultDialog(
    matchedUserId: String,
    commonMovies: List<String>,
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onAddToContacts: () -> Unit
) {
    val context = LocalContext.current
    val sortedMovies = remember(commonMovies) { commonMovies.sorted() }

    // Get display name of matched contact if available
    val matchedDisplayName = contacts
        .firstOrNull { it.contactId == matchedUserId }
        ?.displayName
        ?.takeIf { it.isNotBlank() } ?: matchedUserId

    var highlightedMovie by remember { mutableStateOf<String?>(null) }

    // Shake detection logic
    // AI
    // Get an instance of the SensorManager from the Android system.
    // The SensorManager allows access to physical sensors (like the accelerometer).
    val sensorManager = remember {
        context.getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
    }

    // Get the default accelerometer sensor from the system.
    // The accelerometer measures acceleration force along the x, y, and z axes.
    val accelerometer = remember {
        sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
    }

    // Define a threshold value for acceleration.
    // If the computed acceleration exceeds this threshold, it will be considered a "shake."
    val shakeThreshold = 15f

    // Store the time of the last detected shake in milliseconds.
    // This helps prevent repeated triggers from multiple shake events in a short time.
    var lastShakeTime = remember { 0L }

    // Define the sensor event listener to respond to sensor data changes.
    val sensorListener = remember {
        object : android.hardware.SensorEventListener {

            // Called whenever the sensor's values (x, y, z) change.
            override fun onSensorChanged(event: android.hardware.SensorEvent?) {
                val now = System.currentTimeMillis() // Get the current system time in milliseconds

                // Ensure the event is not null and contains at least three values (x, y, z)
                if (event != null && event.values.size >= 3) {

                    // Read acceleration values along each axis
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    // Calculate total acceleration using the 3D Pythagorean formula
                    val acceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                    // If acceleration exceeds the threshold and enough time has passed since last shake
                    if (acceleration > shakeThreshold && now - lastShakeTime > 1500) {
                        // Update the timestamp of the last shake event
                        lastShakeTime = now

                        // If there are movies to choose from, randomly highlight one
                        if (commonMovies.isNotEmpty()) {
                            highlightedMovie = commonMovies.random()

                            // Show a toast message to indicate that the shake action was triggered
                            Toast.makeText(context, R.string.shake, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Required override, but unused in this case — no need to handle sensor accuracy changes
            override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
        }
    }


    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, accelerometer, android.hardware.SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.match_result)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.match_found, matchedDisplayName, ""))
                Spacer(modifier = Modifier.height(8.dp))

                // Display the list of common movies, highlight if selected
                Column {
                    sortedMovies.forEach { movie ->
                        val displayText = if (movie == highlightedMovie) {
                            stringResource(R.string.highlighted_movie_format, movie)
                        } else {
                            movie
                        }
                        Text(
                            text = displayText,
                            color = if (movie == highlightedMovie) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok), color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onAddToContacts) {
                Text(text = stringResource(R.string.add_to_contacts), color = MaterialTheme.colorScheme.background)
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

/**
 * Dialog showing the result of a group match between the user and selected contacts.
 *
 * Supports shake gesture to highlight a random common movie.
 *
 * @param commonMovies List of common movies among the group
 * @param onDismiss Called when the dialog is closed
 */
@Composable
fun GroupMatchResultDialog(
    commonMovies: List<String>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sortedMovies = remember(commonMovies) { commonMovies.sorted() }
    var highlightedMovie by remember { mutableStateOf<String?>(null) }

    // Shake detection logic
    // AI
    // Get an instance of the SensorManager from the Android system.
    // The SensorManager allows access to physical sensors (like the accelerometer).
    val sensorManager = remember {
        context.getSystemService(android.content.Context.SENSOR_SERVICE) as android.hardware.SensorManager
    }

    // Get the default accelerometer sensor from the system.
    // The accelerometer measures acceleration force along the x, y, and z axes.
    val accelerometer = remember {
        sensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER)
    }

    // Define a threshold value for acceleration.
    // If the computed acceleration exceeds this threshold, it will be considered a "shake."
    val shakeThreshold = 15f

    // Store the time of the last detected shake in milliseconds.
    // This helps prevent repeated triggers from multiple shake events in a short time.
    var lastShakeTime = remember { 0L }

    // Define the sensor event listener to respond to sensor data changes.
    val sensorListener = remember {
        object : android.hardware.SensorEventListener {

            // Called whenever the sensor's values (x, y, z) change.
            override fun onSensorChanged(event: android.hardware.SensorEvent?) {
                val now = System.currentTimeMillis() // Get the current system time in milliseconds

                // Ensure the event is not null and contains at least three values (x, y, z)
                if (event != null && event.values.size >= 3) {

                    // Read acceleration values along each axis
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]

                    // Calculate total acceleration using the 3D Pythagorean formula
                    val acceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                    // If acceleration exceeds the threshold and enough time has passed since last shake
                    if (acceleration > shakeThreshold && now - lastShakeTime > 1500) {
                        // Update the timestamp of the last shake event
                        lastShakeTime = now

                        // If there are movies to choose from, randomly highlight one
                        if (commonMovies.isNotEmpty()) {
                            highlightedMovie = commonMovies.random()

                            // Show a toast message to indicate that the shake action was triggered
                            Toast.makeText(context, R.string.shake, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            // Required override, but unused in this case — no need to handle sensor accuracy changes
            override fun onAccuracyChanged(sensor: android.hardware.Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorListener, accelerometer, android.hardware.SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.match_result)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.group_match_found, ""))
                Spacer(modifier = Modifier.height(8.dp))

                // List of matched movies
                Column {
                    sortedMovies.forEach { movie ->
                        val displayText = if (movie == highlightedMovie) {
                            stringResource(R.string.highlighted_movie_format, movie)
                        } else {
                            movie
                        }
                        Text(
                            text = displayText,
                            color = if (movie == highlightedMovie) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok), color = MaterialTheme.colorScheme.background)
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

/**
 * Dialog allowing the user to select multiple contacts for a group match.
 *
 * @param allContacts All available contacts
 * @param onConfirm Callback with selected contacts when user confirms
 * @param onDismiss Called when the dialog is dismissed
 */
@Composable
fun MultiContactSelectionDialog(
    allContacts: List<Contact>,
    onConfirm: (List<Contact>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val selectedStates = remember { mutableStateListOf(*BooleanArray(allContacts.size).toTypedArray()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_contacts_title)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Display each contact with checkbox
                allContacts.forEachIndexed { index, contact ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedStates[index] = !selectedStates[index] }
                    ) {
                        Checkbox(
                            checked = selectedStates[index],
                            onCheckedChange = { selectedStates[index] = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(contact.displayName.ifBlank { contact.contactId })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedContacts = allContacts.filterIndexed { index, _ -> selectedStates[index] }
                if (selectedContacts.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.no_contacts_selected), Toast.LENGTH_SHORT).show()
                } else {
                    onConfirm(selectedContacts)
                }
            }) {
                Text(text = stringResource(R.string.find_match), color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), color = MaterialTheme.colorScheme.background)
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

/**
 * Dialog for selecting preferred movie genres.
 *
 * Includes "All" option to select/deselect all genres.
 *
 * @param allGenresWithAllOption List of genres including "All" as the first item
 * @param selectedStates Boolean states representing selected genres
 * @param allOptionLabel Label for the "All" option
 * @param onConfirm Called with selected genres when confirmed
 * @param onDismiss Called when dialog is dismissed
 */
@Composable
fun GenreSelectionDialog(
    allGenresWithAllOption: List<String>,
    selectedStates: MutableList<Boolean>,
    allOptionLabel: String,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_genres_title)) },
        text = {
            Column {
                // Genre checkboxes including "All"
                allGenresWithAllOption.forEachIndexed { index, genre ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (index == 0) {
                                    // Toggle all genres if "All" clicked
                                    val newState = !selectedStates[0]
                                    selectedStates.indices.forEach { i -> selectedStates[i] = newState }
                                } else {
                                    // Toggle individual genre and update "All"
                                    selectedStates[index] = !selectedStates[index]
                                    if (!selectedStates[index]) selectedStates[0] = false
                                    selectedStates[0] = selectedStates.drop(1).all { it }
                                }
                            }
                    ) {
                        Checkbox(
                            checked = selectedStates[index],
                            onCheckedChange = {
                                if (index == 0) {
                                    val newState = !selectedStates[0]
                                    selectedStates.indices.forEach { i -> selectedStates[i] = newState }
                                } else {
                                    selectedStates[index] = !selectedStates[index]
                                    if (!selectedStates[index]) selectedStates[0] = false
                                    selectedStates[0] = selectedStates.drop(1).all { it }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = genre)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedGenres = allGenresWithAllOption
                    .filterIndexed { index, _ -> selectedStates[index] }
                    .filter { it != allOptionLabel }
                onConfirm(selectedGenres)
            }) {
                Text(text = stringResource(R.string.confirm), color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel), color = MaterialTheme.colorScheme.background)
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

/**
 * Displays an image related to movies centered at the bottom of the screen.
 */
@Composable
fun AnotherMovieImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            //model = "https://momenvy.co/wp-content/uploads/2017/07/movies-1-622x420.png",
            model = "https://veganbookblogger.com/wp-content/uploads/2024/03/r-2.png?w=2048",
            contentDescription = null,
            modifier = Modifier.width(250.dp)
        )
    }
}
