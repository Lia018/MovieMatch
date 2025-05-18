package com.example.moviematch.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.TextFieldValue
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
 * ViewModel responsible for managing the user profile editing screen.
 * Handles user state, username and password changes, theme preference,
 * and login data cleanup.
 *
 * @param userId ID of the currently logged-in user.
 * @param repository The user repository for accessing and modifying user data.
 */
class EditProfileViewModel(
    private val userId: String,
    private val repository: UserRepository
) : ViewModel() {

    /** Holds the currently loaded user object. */
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    /** Toggles visibility of the old password field. */
    val showOldPassword = MutableStateFlow(false)

    /** Toggles visibility of the new password field. */
    val showNewPassword = MutableStateFlow(false)

    /** Toggles visibility of the user ID field. */
    val showId = MutableStateFlow(false)

    /** Tracks whether dark mode is enabled. */
    val isDarkTheme = MutableStateFlow(false)

    /** Holds the username input field value. */
    val username = MutableStateFlow(TextFieldValue(""))

    /** Holds the current (old) password input field value. */
    val currentPassword = MutableStateFlow(TextFieldValue(""))

    /** Holds the new password input field value. */
    val newPassword = MutableStateFlow(TextFieldValue(""))

    /** Emits UI messages using string resource IDs (e.g., Toasts). */
    private val _message = MutableSharedFlow<Int>()
    val message: SharedFlow<Int> = _message

    /**
     * Initializes the ViewModel by loading the user's data and pre-filling the username field.
     */
    init {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _user.value = user
            username.value = TextFieldValue(user?.username ?: "")
        }
    }

    /**
     * Attempts to update the user's username.
     * Validation includes checking length and uniqueness.
     */
    fun updateUsername() {
        viewModelScope.launch {
            val newName = username.value.text

            if (newName.length < 3) {
                emitMessage(R.string.name_too_short)
                return@launch
            }

            val nameTaken = repository.getUserByUsername(newName)?.userId?.let { it != userId } ?: false
            if (nameTaken) {
                emitMessage(R.string.name_not_unique)
                return@launch
            }

            _user.value?.let {
                val updated = it.copy(username = newName)
                repository.updateUser(updated)
                _user.value = updated
                emitMessage(R.string.name_updated)
            }
        }
    }

    /**
     * Attempts to update the user's password after validating the old password and the new one.
     * Emits appropriate error or success messages.
     *
     * @param onPasswordChanged Optional callback triggered after successful password change.
     */
    fun updatePassword(onPasswordChanged: (String) -> Unit = {}) {
        viewModelScope.launch {
            val user = _user.value ?: return@launch
            val oldPass = currentPassword.value.text
            val newPassText = newPassword.value.text

            when {
                oldPass != user.password -> emitMessage(R.string.incorrect_password)
                newPassText == oldPass -> emitMessage(R.string.password_same)
                newPassText.length < 3 -> emitMessage(R.string.password_too_short)
                else -> {
                    val updated = user.copy(password = newPassText)
                    repository.updateUser(updated)
                    _user.value = updated
                    currentPassword.value = TextFieldValue("")
                    newPassword.value = TextFieldValue("")
                    emitMessage(R.string.password_updated)

                    onPasswordChanged(newPassText)
                }
            }
        }
    }

    /**
     * Deletes the user from the database and performs logout cleanup.
     *
     * @param clearPrefs Lambda to clear any saved preferences or login state.
     * @param onComplete Callback triggered after deletion and cleanup.
     */
    fun deleteUser(clearPrefs: () -> Unit, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUserById(userId)
            clearPrefs()
            emitMessage(R.string.account_deleted)
            onComplete()
        }
    }

    /**
     * Clears saved login information such as username/password from preferences.
     *
     * @param clearPrefs Lambda to perform preference clearing.
     * @param onComplete Callback to execute after clearing.
     */
    fun clearLoginData(clearPrefs: () -> Unit, onComplete: () -> Unit) {
        clearPrefs()
        viewModelScope.launch {
            emitMessage(R.string.login_data_deleted)
        }
        onComplete()
    }

    /**
     * Helper function to emit a message to the UI.
     *
     * @param resId String resource ID of the message.
     */
    private fun emitMessage(@StringRes resId: Int) {
        viewModelScope.launch {
            _message.emit(resId)
        }
    }
}
