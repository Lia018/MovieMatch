package com.example.moviematch.presentation.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.moviematch.R
import com.example.moviematch.presentation.navigation.NavRoute
import com.example.moviematch.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Composable responsible for rendering the login screen UI and managing login logic.
 *
 * Features:
 * - Input fields for user ID and password
 * - Login button
 * - Password visibility toggle
 * - Automatic login with saved credentials
 * - Password recovery and reset dialogs
 * - Option to remember login credentials
 *
 * @param viewModel The ViewModel handling login state and logic.
 * @param navController Navigation controller for navigating between screens.
 * @param prefsName The name of the SharedPreferences file used for storing login data.
 * @param prefillUserId Optional user ID to prefill the input field (e.g., after registration).
 * @param skipAutoLogin Whether to skip automatic login (e.g., after registration).
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController,
    prefsName: String,
    prefillUserId: String? = null,
    skipAutoLogin: Boolean = false
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    // Controls suppression of the "remember login" dialog for a specific user
    var suppressRememberDialog by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("suppressRememberDialog_${prefillUserId.orEmpty()}", false)
        )
    }

    // States tied to ViewModel
    val userId by viewModel.userId.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // UI states
    var isLoginAttemptedAutomatically by remember { mutableStateOf(false) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Dialog controls
    var showForgotDialog by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showRememberDialog by rememberSaveable { mutableStateOf(false) }
    var showRememberOptions by rememberSaveable { mutableStateOf(false) }

    // Temporary storage for pending login
    var pendingUserId by remember { mutableStateOf<String?>(null) }
    var pendingPassword by remember { mutableStateOf<String?>(null) }

    /**
     * Triggers auto-login if credentials are stored and not explicitly skipped.
     * Also pre-fills the userId after registration.
     */
    LaunchedEffect(Unit) {
        if (!skipAutoLogin && !isLoginAttemptedAutomatically) {
            val storedUserId = prefs.getString("userId", null)
            val storedPassword = prefs.getString("password", null)
            if (!storedUserId.isNullOrBlank() && !storedPassword.isNullOrBlank()) {
                isLoginAttemptedAutomatically = true
                viewModel.onUserIdChange(storedUserId)
                viewModel.onPasswordChange(storedPassword)
                viewModel.login()
            }
        } else if (skipAutoLogin && prefillUserId != null) {
            viewModel.onUserIdChange(prefillUserId)
        }
    }

    /**
     * Observes login result and handles navigation or dialog prompting.
     */
    LaunchedEffect(loginSuccess) {
        loginSuccess?.let { user ->
            val hasStored = !prefs.getString("userId", null).isNullOrBlank() &&
                    !prefs.getString("password", null).isNullOrBlank()
            val suppressForThisUser = prefs.getBoolean("suppressRememberDialog_${user.userId}", false)

            if (!hasStored && !suppressForThisUser) {
                pendingUserId = user.userId
                pendingPassword = user.password
                showRememberDialog = true
            } else {
                Toast.makeText(context, context.getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                navController.navigate(NavRoute.Menu.createRoute(user.userId)) {
                    popUpTo(NavRoute.Login.route) { inclusive = true }
                }
            }
        } ?: run {
            if (userId.isNotBlank() && password.isNotBlank()) {
                showError = true
            }
        }
    }

    /**
     * Shows error toast from ViewModel.
     */
    LaunchedEffect(Unit) {
        viewModel.errorMessageId.collect {
            Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Displays the simulated SMS code in development.
     */
    LaunchedEffect(Unit) {
        viewModel.simulatedSmsCode.collect {
            Toast.makeText(context, context.getString(R.string.simulated_sms, it), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Resets login state after password reset success.
     */
    LaunchedEffect(Unit) {
        viewModel.resetSuccess.collect {
            Toast.makeText(context, context.getString(R.string.password_reset_success), Toast.LENGTH_SHORT).show()
            showResetDialog = false
            viewModel.onUserIdChange("")
            viewModel.onPasswordChange("")
            isLoginAttemptedAutomatically = false
            prefs.edit {
                remove("userId")
                remove("password")
            }
        }
    }

    /**
     * Toasts login error messages.
     */
    LaunchedEffect(Unit) {
        viewModel.loginError.collect { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Main Login Screen Layout
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .systemBarsPadding()
    ) {
        // User ID input field
        OutlinedTextField(
            value = userId,
            onValueChange = { viewModel.onUserIdChange(it) },
            label = { Text(context.getString(R.string.enter_id)) },
            singleLine = true,
            modifier = Modifier.align(Alignment.CenterHorizontally).width(350.dp),
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text(context.getString(R.string.enter_password)) },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.align(Alignment.CenterHorizontally).width(350.dp),
            colors = textFieldColors()
        )

        // Show password checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterHorizontally).width(375.dp).padding(top = 12.dp)
        ) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = { showPassword = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                    uncheckedColor = MaterialTheme.colorScheme.onSecondary,
                )
            )
            Text(
                text = context.getString(R.string.show_password),
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.clickable { showPassword = !showPassword }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Login button
        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.align(Alignment.CenterHorizontally).width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(context.getString(R.string.login), color = MaterialTheme.colorScheme.onSecondary, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Forgot password button
        TextButton(
            onClick = { showForgotDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.forgot_password), color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
        }

        // Navigate to registration
        TextButton(
            onClick = { navController.navigate(NavRoute.Register.route) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(context.getString(R.string.create_account), color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
        }
    }

    // Forgot password dialog
    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onSendCode = { username ->
                scope.launch {
                    val success = viewModel.trySendRecoveryCode(username)
                    if (success) {
                        showForgotDialog = false
                        showResetDialog = true
                    }
                }
            }
        )
    }

    // Password reset dialog
    if (showResetDialog) {
        ResetPasswordDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = { code, newPass -> viewModel.resetPassword(code, newPass) }
        )
    }

    // Dialog asking user whether to remember login
    if (showRememberDialog && !suppressRememberDialog) {
        AlertDialog(
            onDismissRequest = { showRememberDialog = false },
            title = { Text(context.getString(R.string.remember_login_title), color = MaterialTheme.colorScheme.onSecondary) },
            text = { Text(context.getString(R.string.remember_login_msg), color = MaterialTheme.colorScheme.onSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    prefs.edit {
                        putString("userId", pendingUserId)
                        putString("password", pendingPassword)
                    }
                    Toast.makeText(context, context.getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoute.Menu.createRoute(pendingUserId!!)) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                }) {
                    Text(context.getString(R.string.yes), color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRememberDialog = false
                    showRememberOptions = true
                }) {
                    Text(context.getString(R.string.no), color = MaterialTheme.colorScheme.background)
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    // Dialog for future remember preferences
    if (showRememberOptions) {
        AlertDialog(
            onDismissRequest = { showRememberOptions = false },
            title = { Text(context.getString(R.string.remember_decision_title)) },
            text = { Text(context.getString(R.string.remember_decision_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    prefs.edit { putBoolean("suppressRememberDialog_${pendingUserId}", true) }
                    suppressRememberDialog = true
                    showRememberOptions = false
                    Toast.makeText(context, context.getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoute.Menu.createRoute(pendingUserId!!)) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                }) {
                    Text(context.getString(R.string.do_not_ask_again), color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRememberOptions = false
                    Toast.makeText(context, context.getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoute.Menu.createRoute(pendingUserId!!)) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                }) {
                    Text(context.getString(R.string.think_about_it), color = MaterialTheme.colorScheme.background)
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }
}

/**
 * A reusable dialog for submitting a username to receive a password recovery code.
 */
@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onSendCode: (String) -> Unit) {
    var username by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = context.getString(R.string.forgot_password_title), color = MaterialTheme.colorScheme.onSecondary) },
        text = {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(context.getString(R.string.forgot_username_label)) },
                singleLine = true,
                modifier = Modifier.width(350.dp),
                colors = textFieldColors()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSendCode(username.trim()) }) {
                Text(context.getString(R.string.send_code), color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.cancel), color = MaterialTheme.colorScheme.background)
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

/**
 * A reusable dialog for resetting password with verification code and new password.
 */
@Composable
fun ResetPasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {},
        title = { Text(context.getString(R.string.reset_password_title), color = MaterialTheme.colorScheme.onSecondary) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(context.getString(R.string.verification_code)) },
                    singleLine = true,
                    modifier = Modifier.width(350.dp),
                    colors = textFieldColors()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(context.getString(R.string.new_password)) },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.width(350.dp),
                    colors = textFieldColors()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(375.dp).padding(top = 12.dp)
                ) {
                    Checkbox(
                        checked = showPassword,
                        onCheckedChange = { showPassword = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            checkmarkColor = MaterialTheme.colorScheme.onSecondary,
                            uncheckedColor = MaterialTheme.colorScheme.onSecondary,
                        )
                    )
                    Text(
                        text = context.getString(R.string.show_password),
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.clickable { showPassword = !showPassword }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(code.trim(), newPassword.trim()) }) {
                Text(context.getString(R.string.confirm), color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(context.getString(R.string.cancel), color = MaterialTheme.colorScheme.background)
            }
        }
    )
}

/**
 * Provides consistent styling for all OutlinedTextFields across the login screen.
 */
@Composable
private fun textFieldColors() = TextFieldDefaults.colors(
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