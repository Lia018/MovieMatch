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

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loginSuccess = MutableStateFlow<User?>(null)
    val loginSuccess: StateFlow<User?> = _loginSuccess

    private val _recoveryCode = MutableStateFlow("")
    private val _recoveryUserId = MutableStateFlow<String?>(null)

    private val _resetSuccess = MutableSharedFlow<Unit>()
    val resetSuccess: SharedFlow<Unit> = _resetSuccess

    private val _errorMessageId = MutableSharedFlow<Int>()
    val errorMessageId: SharedFlow<Int> = _errorMessageId

    private val _simulatedSmsCode = MutableSharedFlow<String>()
    val simulatedSmsCode: SharedFlow<String> = _simulatedSmsCode

    private val _loginError = MutableSharedFlow<Int>()
    val loginError: SharedFlow<Int> = _loginError

    fun onUserIdChange(value: String) {
        _userId.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

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


    private fun emitLoginError(@StringRes resId: Int) {
        viewModelScope.launch {
            _loginError.emit(resId)
        }
    }
}
