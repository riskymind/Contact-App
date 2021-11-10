package com.asterisk.contactapp.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.asterisk.contactapp.data.Contact
import com.asterisk.contactapp.data.ContactRepository
import com.asterisk.contactapp.data.PreferencesManager
import com.asterisk.contactapp.data.SortOrder
import com.asterisk.contactapp.ui.ADD_TASK_RESULT_OK
import com.asterisk.contactapp.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow<String>("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val contactsEventChannel = Channel<ContactsEvent>()
    val contactsEvent = contactsEventChannel.receiveAsFlow()

    private val contactFlow = combine(
        searchQuery, preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        contactRepository.getContacts(query, filterPreferences.sortOrder, filterPreferences.hideFav)
    }

    fun onSortedOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideFavClicked(hideFav: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideFav(hideFav)
    }

    fun onContactSelected(contact: Contact) = viewModelScope.launch {
        contactsEventChannel.send(ContactsEvent.NavigateToEditContactScreen(contact))
    }

    fun onContactSwiped(contact: Contact) = viewModelScope.launch {
        contactRepository.deleteContact(contact)
        contactsEventChannel.send(ContactsEvent.ShowUndoDeletedContactMessage(contact))
    }

    fun undoDeletedContact(contact: Contact) = viewModelScope.launch {
        contactRepository.insertContact(contact)
    }

    fun onAddContactClick() = viewModelScope.launch {
        contactsEventChannel.send(ContactsEvent.NavigateToAddContactScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> {
                showContactSaveConfirmation("Contact Added")
            }
            EDIT_TASK_RESULT_OK -> {
                showContactSaveConfirmation("Contact Updated")
            }
        }
    }

    private fun showContactSaveConfirmation(s: String) = viewModelScope.launch {
        contactsEventChannel.send(ContactsEvent.ShowContactSaveConfirmationMessage(s))
    }

    fun deleteAllContacts() = viewModelScope.launch{
        contactsEventChannel.send(ContactsEvent.NavigateToDeleteAllContactsScreen)
    }


    val contacts = contactFlow.asLiveData()


    sealed class ContactsEvent {
        object NavigateToAddContactScreen : ContactsEvent()
        data class NavigateToEditContactScreen(val contact: Contact) : ContactsEvent()
        data class ShowUndoDeletedContactMessage(val contact: Contact) : ContactsEvent()
        data class ShowContactSaveConfirmationMessage(val msg: String) : ContactsEvent()
        object NavigateToDeleteAllContactsScreen: ContactsEvent()
    }

}
