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
 * ViewModel responsible for handling user login, password recovery, and reset functionality.
 *
 * It exposes state related to login fields, success or error messages, and handles
 * business logic around verifying credentials and resetting passwords.
 *
 * @property userRepository The repository used to interact with user data.
 */
class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    /** Input field value for user ID. */
    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    /** Input field value for password. */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    /** Emits the logged-in user upon successful login. */
    private val _loginSuccess = MutableStateFlow<User?>(null)
    val loginSuccess: StateFlow<User?> = _loginSuccess

    /** Stores the recovery code sent to the user. */
    private val _recoveryCode = MutableStateFlow("")

    /** Stores the user ID to which the recovery code belongs. */
    private val _recoveryUserId = MutableStateFlow<String?>(null)

    /** Emits when the password reset process completes successfully. */
    private val _resetSuccess = MutableSharedFlow<Unit>()
    val resetSuccess: SharedFlow<Unit> = _resetSuccess

    /** Emits error messages using string resource IDs (e.g. for Toasts). */
    private val _errorMessageId = MutableSharedFlow<Int>()
    val errorMessageId: SharedFlow<Int> = _errorMessageId

    /** Emits a simulated SMS recovery code for display purposes (mock behavior). */
    private val _simulatedSmsCode = MutableSharedFlow<String>()
    val simulatedSmsCode: SharedFlow<String> = _simulatedSmsCode

    /** Emits login-specific error messages. */
    private val _loginError = MutableSharedFlow<Int>()
    val loginError: SharedFlow<Int> = _loginError

    /**
     * Updates the user ID field with a new value.
     *
     * @param value The new user ID string.
     */
    fun onUserIdChange(value: String) {
        _userId.value = value
    }

    /**
     * Updates the password field with a new value.
     *
     * @param value The new password string.
     */
    fun onPasswordChange(value: String) {
        _password.value = value
    }

    /**
     * Validates the user ID and password, attempts login, and emits the result.
     *
     * - Validates for blank fields.
     * - Checks ID length (must be 6 digits).
     * - Verifies password match.
     * - Emits appropriate error or success state.
     */
    fun login() {
        viewModelScope.launch {
            val userId = _userId.value.trim()
            val password = _password.value

            if (userId.isBlank() || password.isBlank()) {
                emitLoginError(R.string.fill_in_login_details)
                _loginSuccess.value = null
                return@launch
            }

            if (userId.length != 6) {
                emitLoginError(R.string.invalid_user_id_length)
                _loginSuccess.value = null
                return@launch
            }

            val user = userRepository.getUserById(userId)
            if (user == null) {
                emitLoginError(R.string.not_found)
                _loginSuccess.value = null
                return@launch
            }

            if (user.password != password) {
                emitLoginError(R.string.invalid_login)
                _loginSuccess.value = null
                return@launch
            }

            _loginSuccess.value = user
        }
    }

    /**
     * Tries to initiate a recovery process for a given username.
     *
     * - Emits a simulated SMS code if the username exists.
     * - Stores the recovery code and associated user ID for later verification.
     *
     * @param username The username provided by the user.
     * @return `true` if recovery code was sent; `false` if user not found or input was invalid.
     */
    suspend fun trySendRecoveryCode(username: String): Boolean {
        val trimmed = username.trim()
        if (trimmed.isEmpty()) {
            _errorMessageId.emit(R.string.username_required)
            return false
        }

        val user = userRepository.getUserByUsername(trimmed)
        return if (user != null) {
            val code = (1000..9999).random().toString()
            _recoveryUserId.value = user.userId
            _recoveryCode.value = code
            _simulatedSmsCode.emit(code)
            true
        } else {
            _errorMessageId.emit(R.string.user_not_found)
            false
        }
    }

    /**
     * Validates the recovery code and updates the password if valid.
     *
     * - Checks if recovery code matches.
     * - Validates new password constraints.
     * - Updates password in repository.
     * - Emits a reset success signal on completion.
     *
     * @param enteredCode The code entered by the user.
     * @param newPassword The new password to set.
     */
    fun resetPassword(enteredCode: String, newPassword: String) {
        viewModelScope.launch {
            val userId = _recoveryUserId.value
            val expectedCode = _recoveryCode.value

            if (userId == null) return@launch

            if (enteredCode != expectedCode) {
                _errorMessageId.emit(R.string.invalid_code)
                return@launch
            }

            if (newPassword.length < 3) {
                _errorMessageId.emit(R.string.password_too_short)
                return@launch
            }

            val user = userRepository.getUserById(userId)
            if (user == null) {
                _errorMessageId.emit(R.string.user_not_found)
                return@launch
            }

            if (user.password == newPassword) {
                _errorMessageId.emit(R.string.same_password)
                return@launch
            }

            userRepository.updateUser(user.copy(password = newPassword))

            _password.value = newPassword
            _userId.value = userId

            _resetSuccess.emit(Unit)
        }
    }

    /**
     * Emits a login-specific error message to be shown in the UI.
     *
     * @param resId The string resource ID of the error message.
     */
    private fun emitLoginError(@StringRes resId: Int) {
        viewModelScope.launch {
            _loginError.emit(resId)
        }
    }
}
