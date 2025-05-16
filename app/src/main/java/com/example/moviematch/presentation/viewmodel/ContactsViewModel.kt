package com.example.moviematch.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviematch.R
import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.domain.repository.ContactRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val repository: ContactRepository,
    private val userId: String
) : ViewModel() {

    val contactInput = MutableStateFlow(TextFieldValue(""))
    private val _contactInput = MutableStateFlow("")

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact

    val editedName = MutableStateFlow("")

    private val _event = MutableSharedFlow<Int>()
    val event: SharedFlow<Int> = _event

    fun onContactInputChange(newValue: String) {
        contactInput.value = TextFieldValue(
            text = newValue,
            selection = TextRange(newValue.length)
        )
    }

    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
        editedName.value = contact.displayName
    }

    fun clearDialogs() {
        _selectedContact.value = null
    }

    fun loadContacts() {
        viewModelScope.launch {
            _contacts.value = repository.getContactsForUser(userId)
        }
    }

    fun addContact() {
        val id = contactInput.value.text.trim()
        if (id.isBlank()) {
            emitEvent(R.string.enter_id_first)
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
            } else {
                repository.addContact(Contact(ownerId = userId, contactId = id))
                contactInput.value = TextFieldValue("")
                loadContacts()
            }
        }
    }


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

    fun deleteSelectedContact() {
        viewModelScope.launch {
            val contact = _selectedContact.value ?: return@launch
            repository.deleteContact(contact.ownerId, contact.contactId)
            loadContacts()
            emitEvent(R.string.contact_deleted)
            clearDialogs()
        }
    }

    private fun emitEvent(@StringRes resId: Int) {
        viewModelScope.launch {
            _event.emit(resId)
        }
    }

}
