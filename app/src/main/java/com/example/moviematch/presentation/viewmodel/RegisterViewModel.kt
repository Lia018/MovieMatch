package com.example.moviematch.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviematch.R
import com.example.moviematch.data.db.entity.User
import com.example.moviematch.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for handling user registration logic.
 *
 * Manages input validation, username availability check, user ID generation,
 * and communication with the [UserRepository] to insert new users.
 *
 * Emits registration result and error messages as UI events.
 *
 * @param repository The repository that provides user-related operations.
 */
class RegisterViewModel(
    private val repository: UserRepository
) : ViewModel() {

    /** User-entered name input as [StateFlow]. */
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    /** User-entered password input as [StateFlow]. */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    /**
     * Emits one-time error messages represented by string resource IDs.
     *
     * UI should observe this [SharedFlow] to show error toasts or dialogs.
     */
    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage: SharedFlow<Int> = _errorMessage

    /**
     * Emits the generated user ID on successful registration.
     *
     * UI should observe this [SharedFlow] to proceed with navigation or confirmation.
     */
    private val _registrationSuccess = MutableSharedFlow<String>()
    val registrationSuccess: SharedFlow<String> = _registrationSuccess

    /**
     * Updates the name field when the user modifies the input.
     *
     * @param value New name entered by the user.
     */
    fun onNameChange(value: String) {
        _name.value = value
    }

    /**
     * Updates the password field when the user modifies the input.
     *
     * @param value New password entered by the user.
     */
    fun onPasswordChange(value: String) {
        _password.value = value
    }

    /**
     * Validates input and attempts to register a new user.
     *
     * - Ensures both name and password have at least 3 characters.
     * - Checks if the username already exists.
     * - Generates a random 6-digit user ID that doesn't conflict.
     * - Stores the new user in the database on success.
     * - Emits appropriate events for success or failure.
     */
    fun registerUser() {
        val trimmedName = name.value.trim()
        val trimmedPassword = password.value.trim()

        if (trimmedName.length < 3 || trimmedPassword.length < 3) {
            emitError(R.string.min_length_error)
            return
        }

        viewModelScope.launch {
            val exists = repository.getUserByUsername(trimmedName)
            if (exists != null) {
                emitError(R.string.name_exists)
                return@launch
            }

            // Generate a unique 6-digit user ID
            var userId: String
            do {
                userId = (100000..999999).random().toString()
            } while (repository.getUserById(userId) != null)

            // Insert new user into the database
            repository.insertUser(User(userId, trimmedName, trimmedPassword))

            // Notify UI of successful registration
            _registrationSuccess.emit(userId)
        }
    }

    /**
     * Emits a one-time error message to be handled by the UI.
     *
     * @param messageResId String resource ID of the error message to show.
     */
    private fun emitError(@StringRes messageResId: Int) {
        viewModelScope.launch {
            _errorMessage.emit(messageResId)
        }
    }
}
