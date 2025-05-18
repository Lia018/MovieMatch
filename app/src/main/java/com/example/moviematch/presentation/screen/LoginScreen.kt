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
import androidx.compose.ui.res.stringResource
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

    var suppressRememberDialog by rememberSaveable {
        mutableStateOf(
            prefs.getBoolean("suppressRememberDialog_${prefillUserId.orEmpty()}", false)
        )
    }

    val userId by viewModel.userId.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    val scrollState = rememberScrollState()

    var isLoginAttemptedAutomatically by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    var showForgotDialog by rememberSaveable { mutableStateOf(false) }
    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    var showRememberDialog by remember { mutableStateOf(false) }
    var pendingUserId by remember { mutableStateOf<String?>(null) }
    var pendingPassword by remember { mutableStateOf<String?>(null) }

    var showRememberOptions by rememberSaveable { mutableStateOf(false) }

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

    LaunchedEffect(Unit) {
        viewModel.errorMessageId.collect {
            Toast.makeText(context, context.getString(it), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.simulatedSmsCode.collect {
            Toast.makeText(context, context.getString(R.string.simulated_sms, it), Toast.LENGTH_LONG).show()
        }
    }

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


    LaunchedEffect(Unit) {
        viewModel.loginError.collect { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .systemBarsPadding()
    ) {
        OutlinedTextField(
            value = userId,
            onValueChange = { viewModel.onUserIdChange(it) },
            label = { Text(stringResource(R.string.enter_id)) },
            singleLine = true,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp),
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

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text(stringResource(R.string.enter_password)) },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp),
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(375.dp)
                .padding(top = 8.dp)

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
                text = stringResource(R.string.show_password),
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .clickable { showPassword = !showPassword }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                stringResource(R.string.login),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { showForgotDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                stringResource(R.string.forgot_password),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(0.dp))

        TextButton(
            onClick = { navController.navigate(NavRoute.Register.route) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                stringResource(R.string.create_account),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp
            )
        }
    }

    val scope = rememberCoroutineScope()

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

    if (showResetDialog) {
        ResetPasswordDialog(
            onDismiss = { showResetDialog = false },
            onConfirm = { code, newPass -> viewModel.resetPassword(code, newPass) }
        )
    }

    if (showRememberDialog && !suppressRememberDialog && pendingUserId != null && pendingPassword != null) {
        AlertDialog(
            onDismissRequest = { showRememberDialog = false },
            title = {
                Text(
                    stringResource(R.string.remember_login_title),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            },
            text = {
                Text(
                    stringResource(R.string.remember_login_msg),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    context.getSharedPreferences(prefsName, Context.MODE_PRIVATE).edit {
                        putString("userId", pendingUserId)
                            .putString("password", pendingPassword)
                    }
                    Toast.makeText(context, context.getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoute.Menu.createRoute(pendingUserId!!)) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                }) {
                    Text(stringResource(R.string.yes),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRememberDialog = false
                    showRememberOptions = true
                }) {
                    Text(stringResource(R.string.no), color = MaterialTheme.colorScheme.background)
                }

            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

    if (showRememberOptions) {
        AlertDialog(
            onDismissRequest = { showRememberOptions = false },
            title = { Text(stringResource(R.string.remember_decision_title)) },
            text = { Text(stringResource(R.string.remember_decision_msg)) },
            confirmButton = {
                TextButton(onClick = {
                    prefs.edit { putBoolean("suppressRememberDialog_${pendingUserId!!}", true) }
                    suppressRememberDialog = true
                    showRememberOptions = false
                    Toast.makeText(context, context.getString(R.string.welcome_back), Toast.LENGTH_SHORT).show()
                    navController.navigate(NavRoute.Menu.createRoute(pendingUserId!!)) {
                        popUpTo(NavRoute.Login.route) { inclusive = true }
                    }
                }) {
                    Text(stringResource(R.string.do_not_ask_again), color = MaterialTheme.colorScheme.background)
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
                    Text(stringResource(R.string.think_about_it),
                        color = MaterialTheme.colorScheme.background)
                }
            },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        )
    }

}


@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onSendCode: (String) -> Unit) {
    var username by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.forgot_password_title),
                color = MaterialTheme.colorScheme.onSecondary
            )
        },
        text = {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.forgot_username_label)) },
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
        },
        confirmButton = {
            TextButton(onClick = { onSendCode(username.trim()) }) {
                Text(
                    text = stringResource(R.string.send_code),
                    //color = MaterialTheme.colorScheme.primary)
                    color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.background)
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

@Composable
fun ResetPasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.reset_password_title),
                color = MaterialTheme.colorScheme.onSecondary
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(stringResource(R.string.verification_code)) },
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
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text(stringResource(R.string.new_password)) },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(375.dp)
                        .padding(top = 8.dp)
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
                        text = stringResource(R.string.show_password),
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .clickable { showPassword = !showPassword }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(code.trim(), newPassword.trim()) }) {
                Text(text = stringResource(R.string.confirm),
                    color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.background)
            }
        }
    )
}
