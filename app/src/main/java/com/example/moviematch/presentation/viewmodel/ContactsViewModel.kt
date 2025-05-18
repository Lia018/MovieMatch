package com.example.moviematch.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviematch.R
import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.domain.repository.ContactRepository
import com.example.moviematch.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing user's contact list.
 *
 * Handles logic related to:
 * - Adding new contacts
 * - Editing and updating contact names
 * - Deleting contacts
 * - Contact selection and UI state tracking
 *
 * @param repository Repository to manage contact persistence operations
 * @param userRepo Repository to retrieve user details
 * @param userId ID of the currently logged-in user
 */
class ContactsViewModel(
    private val repository: ContactRepository,
    private val userRepo: UserRepository,
    private val userId: String
) : ViewModel() {

    /** Current value of the user ID input field */
    val contactInput = MutableStateFlow(TextFieldValue(""))

    /** List of contacts loaded for the current user */
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    /** The currently selected contact, if any */
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact

    /** Temporarily stores the name being edited for the selected contact */
    val editedName = MutableStateFlow("")

    /** Emits UI events like showing error/success toasts based on string resources */
    private val _event = MutableSharedFlow<Int>()
    val event: SharedFlow<Int> = _event

    /**
     * Updates the contact input text value and maintains cursor at the end.
     *
     * @param newValue The new input string entered by the user.
     */
    fun onContactInputChange(newValue: String) {
        contactInput.value = TextFieldValue(
            text = newValue,
            selection = TextRange(newValue.length)
        )
    }

    /**
     * Selects a contact from the list to show options (edit/delete).
     *
     * @param contact The contact to be selected.
     */
    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
        editedName.value = contact.displayName
    }

    /**
     * Clears the currently selected contact, dismissing any dialogs.
     */
    fun clearDialogs() {
        _selectedContact.value = null
    }

    /**
     * Loads all contacts for the current user from the repository.
     */
    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = repository.getContactsForUser(userId)
        }
    }

    /**
     * Attempts to add a new contact to the user's contact list.
     *
     * Validates:
     * - Empty input
     * - Self-addition
     * - Valid ID length
     * - User existence
     * - Duplicate contact
     *
     * Emits appropriate error or success events.
     */
    fun addContact() {
        val id = contactInput.value.text.trim()
        if (id.isBlank()) {
            emitEvent(R.string.enter_id_first)
            return
        }

        if (userId.length != 6) {
            emitEvent(R.string.invalid_user_id_length)
            return
        }

        if (id == userId) {
            emitEvent(R.string.cannot_add_self)
            return
        }

        viewModelScope.launch {
            val exists = repository.getContactsForUser(userId).any { it.contactId == id }
            if (exists) {
                emitEvent(R.string.contact_exists)
                return@launch
            }

            val userExists = userRepo.getUserById(id) != null
            if (!userExists) {
                emitEvent(R.string.not_found)
                return@launch
            }

            repository.addContact(
                Contact(ownerId = userId, contactId = id, addedAt = System.currentTimeMillis())
            )
            contactInput.value = TextFieldValue("")
            loadContacts()
        }
    }

    /**
     * Saves the updated display name for the selected contact.
     * If the contact doesn't exist, it will be added.
     *
     * Also reloads the contact list and dismisses the edit dialog.
     */
    fun saveEditedName() {
        viewModelScope.launch {
            val contact = _selectedContact.value ?: return@launch

            val updatedContact = contact.copy(displayName = editedName.value.trim())
            val exists = repository.getContact(userId, contact.contactId)
            if (exists != null) {
                repository.updateContact(updatedContact)
            } else {
                repository.addContact(updatedContact)
            }
            loadContacts()
            emitEvent(R.string.name_saved)
            clearDialogs()
        }
    }

    /**
     * Deletes the currently selected contact and updates the contact list.
     */
    fun deleteSelectedContact() {
        viewModelScope.launch {
            val contact = _selectedContact.value ?: return@launch
            repository.deleteContact(contact.ownerId, contact.contactId)
            loadContacts()
            emitEvent(R.string.contact_deleted)
            clearDialogs()
        }
    }

    /**
     * Emits a one-time event to be observed by the UI, usually for feedback messages.
     *
     * @param resId Resource ID of the string message to be shown.
     */
    private fun emitEvent(@StringRes resId: Int) {
        viewModelScope.launch {
            _event.emit(resId)
        }
    }
}
