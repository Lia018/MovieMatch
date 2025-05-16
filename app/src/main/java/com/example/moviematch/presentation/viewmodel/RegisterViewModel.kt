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

class RegisterViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage: SharedFlow<Int> = _errorMessage

    private val _registrationSuccess = MutableSharedFlow<String>()
    val registrationSuccess: SharedFlow<String> = _registrationSuccess

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

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

            var userId: String
            do {
                userId = (100000..999999).random().toString()
            } while (repository.getUserById(userId) != null)

            repository.insertUser(User(userId, trimmedName, trimmedPassword))
            _registrationSuccess.emit(userId)
        }
    }

    private fun emitError(@StringRes messageResId: Int) {
        viewModelScope.launch { _errorMessage.emit(messageResId) }
    }
}
