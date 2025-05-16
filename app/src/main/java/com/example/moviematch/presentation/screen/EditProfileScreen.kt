package com.example.moviematch.presentation.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.moviematch.R
import com.example.moviematch.data.db.AppDatabase
import com.example.moviematch.data.repository.UserRepositoryImpl
import com.example.moviematch.presentation.factory.EditProfileViewModelFactory
import com.example.moviematch.presentation.navigation.NavRoute
import com.example.moviematch.presentation.viewmodel.EditProfileViewModel

@Composable
fun EditProfileScreen(
    userId: String,
    navController: NavController,
    onThemeToggle: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val userRepo = remember { UserRepositoryImpl(db.userDao()) }

    val viewModel: EditProfileViewModel = viewModel(
        factory = EditProfileViewModelFactory(userId, repository = userRepo)
    )

    val username by viewModel.username.collectAsState()
    val currentPass by viewModel.currentPassword.collectAsState()
    val newPass by viewModel.newPassword.collectAsState()
    val showOld by viewModel.showOldPassword.collectAsState()
    val showNew by viewModel.showNewPassword.collectAsState()
    val showId by viewModel.showId.collectAsState()
    val isDark by viewModel.isDarkTheme.collectAsState()
    val showDelete by viewModel.showDeleteDialog.collectAsState()
    val showClear by viewModel.showClearDataDialog.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.background)
    //val textColor by animateColorAsState(targetValue = MaterialTheme.colorScheme.onPrimary)

    val selectedLanguage = remember { mutableStateOf("en") }

    val prefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
    val storedUserId = prefs.getString("userId", null)

    val languages = listOf("sk", "en")

    val scrollState = rememberScrollState()



    LaunchedEffect(Unit) {
        viewModel.message.collect { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            //.background(backgroundColor)
            .verticalScroll(scrollState)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.username.value = it },
            label = { Text(stringResource(R.string.name)) },
            singleLine = true,
            modifier = Modifier.width(350.dp),
            colors = customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionButton(stringResource(R.string.save_name)) {
            viewModel.updateUsername()
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = if (showId) userId else "••••••",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.user_id_label)) },
            singleLine = true,
            modifier = Modifier.width(350.dp),
            colors = customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.show_id_hold),
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            modifier = Modifier
                .width(350.dp)
                .background(MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.extraLarge)
                //.height(48.dp)
                .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
                .padding(ButtonDefaults.ContentPadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            viewModel.showId.value = true
                            try {
                                tryAwaitRelease()
                            } finally {
                                viewModel.showId.value = false
                            }
                        }
                    )
                }
                .wrapContentHeight(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            label = stringResource(R.string.enter_current_password),
            value = currentPass,
            onValueChange = { viewModel.currentPassword.value = it },
            isVisible = showOld,
            onToggleVisibility = { viewModel.showOldPassword.value = !showOld }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            label = stringResource(R.string.enter_new_password),
            value = newPass,
            onValueChange = { viewModel.newPassword.value = it },
            isVisible = showNew,
            onToggleVisibility = { viewModel.showNewPassword.value = !showNew }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(stringResource(R.string.change_password)) {
            viewModel.updatePassword { newPassword ->
                context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE).edit {
                    putString("password", newPassword)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .width(350.dp)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(stringResource(R.string.choose_language))

            languages.forEach { langCode ->
                val isSelected = selectedLanguage.value == langCode
                val textColor = MaterialTheme.colorScheme.onSecondary

                Button(
                    onClick = {
                        selectedLanguage.value = langCode
                        prefs.edit().putString("lang", langCode).apply()
                        onLanguageChange(langCode)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = langCode.uppercase(),
                        color = MaterialTheme.colorScheme.onSecondary
                        //color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .width(350.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(text = stringResource(R.string.theme))

            Switch(
                checked = isDark,
                modifier = Modifier.scale(1.3f),
                onCheckedChange = { checked ->
                    viewModel.isDarkTheme.value = checked
                    onThemeToggle()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ActionButton(stringResource(R.string.clear_login_data)) {
            if (storedUserId != null && storedUserId == userId) {
                showClearDataDialog = true
            } else {
                Toast.makeText(context, context.getString(R.string.no_login_data), Toast.LENGTH_SHORT).show()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        ActionButton(stringResource(R.string.delete_account)) {
            showDeleteDialog = true
        }

        Spacer(modifier = Modifier.height(16.dp))

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
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.confirm_delete_account_title)) },
            text = { Text(stringResource(R.string.confirm_delete_account_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.showDeleteDialog.value = false
                    viewModel.deleteUser(
                        clearPrefs = {
                            context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE).edit { clear() }
                        },
                        onComplete = {
                            navController.navigate(NavRoute.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }) {
                    Text(stringResource(R.string.yes_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteDialog.value = false }) {
                    Text(stringResource(R.string.no_keep))
                }
            }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(stringResource(R.string.clear_data_title)) },
            text = { Text(stringResource(R.string.clear_data_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.showClearDataDialog.value = false
                    viewModel.clearLoginData(
                        clearPrefs = {
                            context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE).edit { clear() }
                        },
                        onComplete = {
                            navController.navigate(NavRoute.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showClearDataDialog.value = false }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}

@Composable
fun PasswordField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.width(375.dp),
            colors = customTextFieldColors()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(375.dp)
                .padding(top = 8.dp)
        ) {
            Checkbox(
                checked = isVisible,
                onCheckedChange = { onToggleVisibility() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedColor = MaterialTheme.colorScheme.onSecondary,
                )
            )
            Text(
                text = stringResource(R.string.show_password),
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.clickable { onToggleVisibility() }
            )
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(350.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSecondary, fontSize = 18.sp)
    }
}

@Composable
fun customTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
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
}
