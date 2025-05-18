package com.example.moviematch.presentation.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moviematch.R
import com.example.moviematch.presentation.navigation.NavRoute
import com.example.moviematch.presentation.viewmodel.RegisterViewModel

/**
 * Composable function representing the user registration screen.
 *
 * This screen allows a new user to input a name and password to register.
 * It handles form submission, success feedback, and error display using toasts.
 *
 * @param navController Navigation controller used for routing to other screens.
 * @param viewModel ViewModel containing the registration logic and state.
 */
@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel) {
    val context = LocalContext.current

    // Collect state from ViewModel
    val name by viewModel.name.collectAsState()
    val password by viewModel.password.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    /**
     * Observes and displays error messages emitted from the ViewModel using Toasts.
     */
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect { resId ->
            Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles successful registration by showing a Toast and navigating to the login screen
     * with the new user ID pre-filled.
     */
    LaunchedEffect(Unit) {
        viewModel.registrationSuccess.collect { userId ->
            Toast.makeText(
                context,
                context.getString(R.string.registered_id, userId),
                Toast.LENGTH_LONG
            ).show()

            // Navigate to login with pre-filled user ID and flag to skip auto-login
            navController.navigate(NavRoute.Login.route + "?prefill=$userId&fromRegister=true") {
                popUpTo(NavRoute.Register.route) { inclusive = true }
            }
        }
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Username input field
        OutlinedTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = { Text(stringResource(R.string.enter_name)) },
            singleLine = true,
            modifier = Modifier.width(350.dp),
            colors = themedTextFieldColors()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password input field with optional visibility toggle
        OutlinedTextField(
            value = password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text(stringResource(R.string.enter_password)) },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.width(350.dp),
            colors = themedTextFieldColors()
        )

        // Password visibility toggle
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
                    uncheckedColor = MaterialTheme.colorScheme.onSecondary
                )
            )
            Text(
                text = stringResource(R.string.show_password),
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.clickable { showPassword = !showPassword }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Register button
        Button(
            onClick = { viewModel.registerUser() },
            modifier = Modifier.width(350.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(R.string.register),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation back to Login screen
        TextButton(
            onClick = {
                navController.navigate(NavRoute.Login.route + "?fromRegister=true") {
                    popUpTo(NavRoute.Register.route) { inclusive = true }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.already_have_account),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Returns themed colors for text fields that match the application's MaterialTheme.
 */
@Composable
private fun themedTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary,
        focusedTextColor = MaterialTheme.colorScheme.onSecondary,
        unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
        focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}
