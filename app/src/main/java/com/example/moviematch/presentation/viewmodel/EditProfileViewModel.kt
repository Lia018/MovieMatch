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

class EditProfileViewModel(
    private val userId: String,
    private val repository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    val showOldPassword = MutableStateFlow(false)
    val showNewPassword = MutableStateFlow(false)
    val showId = MutableStateFlow(false)
    val isDarkTheme = MutableStateFlow(false)
    val showDeleteDialog = MutableStateFlow(false)
    val showClearDataDialog = MutableStateFlow(false)

    val username = MutableStateFlow(TextFieldValue(""))
    val currentPassword = MutableStateFlow(TextFieldValue(""))
    val newPassword = MutableStateFlow(TextFieldValue(""))

    private val _message = MutableSharedFlow<Int>()
    val message: SharedFlow<Int> = _message

    init {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _user.value = user
            username.value = TextFieldValue(user?.username ?: "")
        }
    }

    fun updateUsername() {
        viewModelScope.launch {
            val newName = username.value.text

            if (newName.length < 3) {
                emitMessage(R.string.name_too_short)
                return@launch
            }

            val exists = repository.getUserByUsername(newName)?.userId?.let { it != userId } ?: false
            if (exists) {
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



    fun deleteUser(clearPrefs: () -> Unit, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteUserById(userId)
            clearPrefs()
            emitMessage(R.string.account_deleted)
            onComplete()
        }
    }

    fun clearLoginData(clearPrefs: () -> Unit, onComplete: () -> Unit) {
        clearPrefs()
        viewModelScope.launch {
            emitMessage(R.string.login_data_deleted)
        }
        onComplete()
    }

    private fun emitMessage(@StringRes resId: Int) {
        viewModelScope.launch {
            _message.emit(resId)
        }
    }
}
