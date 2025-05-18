package com.example.moviematch.presentation.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.R
import com.example.moviematch.data.db.AppDatabase
import com.example.moviematch.data.repository.ContactRepositoryImpl
import com.example.moviematch.data.repository.UserRepositoryImpl
import com.example.moviematch.presentation.factory.ContactsViewModelFactory
import com.example.moviematch.presentation.viewmodel.ContactsViewModel

/**
 * Displays the screen for managing user contacts.
 *
 * Users can add contacts by entering a user ID, view their list of contacts, and
 * perform actions like editing or deleting existing ones.
 *
 * @param userId The current logged-in user's ID.
 * @param navController Navigation controller to allow returning to the main menu.
 */
@Composable
fun ContactsScreen(userId: String, navController: NavController) {
    val context = LocalContext.current

    // Initialize database and repositories
    val db = AppDatabase.getDatabase(context)
    val contactRepository = remember { ContactRepositoryImpl(db.contactDao()) }
    val userRepository = remember { UserRepositoryImpl(db.userDao()) }

    // ViewModel setup with factory
    val viewModel: ContactsViewModel = viewModel(
        factory = ContactsViewModelFactory(contactRepository, userRepository, userId)
    )

    // UI state holders
    val contactInput by viewModel.contactInput.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val selectedContact by viewModel.selectedContact.collectAsState()

    // Dialog visibility flags
    val showInitialDialog = remember { derivedStateOf { selectedContact != null } }
    var showActionDialog by rememberSaveable { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    // Load initial data and observe events
    LaunchedEffect(Unit) {
        viewModel.loadContacts()
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    // Main UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input for adding a contact by user ID
        OutlinedTextField(
            value = contactInput,
            onValueChange = { viewModel.onContactInputChange(it.text) },
            label = { Text(stringResource(R.string.enter_user_id)) },
            singleLine = true,
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

        Spacer(modifier = Modifier.height(8.dp))

        // Button to add a contact
        Button(
            onClick = viewModel::addContact,
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.add_contact), color = MaterialTheme.colorScheme.onSecondary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of existing contacts
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(contacts) { _, contact ->
                val display = contact.displayName.ifBlank { contact.contactId }
                Text(
                    text = display,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .width(350.dp)
                        .clickable { viewModel.selectContact(contact) }
                        .padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation button back to the main menu
        Button(
            onClick = { navController.navigate("menu/$userId") },
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(stringResource(R.string.back_to_menu), color = MaterialTheme.colorScheme.onSecondary)
        }
    }

    // Dialog: Contact selected (initial)
    if (showInitialDialog.value) {
        AlertDialog(
            onDismissRequest = viewModel::clearDialogs,
            title = { Text(stringResource(R.string.contact_title, selectedContact?.displayName ?: "")) },
            text = { Text(stringResource(R.string.contact_selected, selectedContact?.contactId ?: "")) },
            confirmButton = {
                TextButton(onClick = { showActionDialog = true }) {
                    Text(text = stringResource(R.string.edit_or_delete),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::clearDialogs) {
                    Text(text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    // Dialog: Choose action (edit/delete)
    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text(stringResource(R.string.choose_action)) },
            text = { Text(stringResource(R.string.edit_or_delete_question)) },
            confirmButton = {
                TextButton(onClick = {
                    showActionDialog = false
                    showEditDialog = true
                }) {
                    Text(text = stringResource(R.string.edit),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showActionDialog = false
                    showDeleteDialog = true
                }) {
                    Text(text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    // Dialog: Edit contact
    if (showEditDialog) {
        var tempName by rememberSaveable(selectedContact?.contactId) {
            mutableStateOf(selectedContact?.displayName ?: "")
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.edit_contact)) },
            text = {
                Column {
                    Text(stringResource(R.string.contact_id_label, selectedContact?.contactId ?: ""))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(stringResource(R.string.contact_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editedName.value = tempName
                    viewModel.saveEditedName()
                    showEditDialog = false
                }) {
                    Text(
                        text = stringResource(R.string.save),
                        color = MaterialTheme.colorScheme.background
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.background
                    )
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    // Dialog: Delete contact confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_contact)) },
            text = {
                Text(stringResource(R.string.confirm_delete_contact, selectedContact?.contactId ?: ""))
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSelectedContact()
                    showDeleteDialog = false
                }) {
                    Text(text = stringResource(R.string.yes_delete),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}
