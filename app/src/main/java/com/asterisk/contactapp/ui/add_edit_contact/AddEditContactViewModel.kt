package com.asterisk.contactapp.ui.add_edit_contact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.contactapp.data.Contact
import com.asterisk.contactapp.data.ContactRepository
import com.asterisk.contactapp.ui.ADD_TASK_RESULT_OK
import com.asterisk.contactapp.ui.EDIT_TASK_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    private val state: SavedStateHandle
) : ViewModel() {


    private val addEditContactEventChannel = Channel<AddEditContactEvent>()
    val addEditContactEvent = addEditContactEventChannel.receiveAsFlow()

    val contact = state.get<Contact>("contact")

    var contactName = state.get<String>("contactName") ?: contact?.name ?: ""
        set(value) {
            field = value
            state.set("contactName", value)
        }

    var contactPhone = state.get<String>("contactPhone") ?: contact?.phone ?: ""
        set(value) {
            field = value
            state.set("contactPhone", value)
        }

    var contactImage = state.get<String>("contactImage") ?: contact?.image ?: ""
        set(value) {
            field = value
            state.set("contactImage", value)
        }

    var isFav = state.get<Boolean>("contactFav") ?: contact?.favorite ?: false
        set(value) {
            field = value
            state.set("contactFav", value)
        }

    fun onSaveClick() {
        if (contactName.isBlank()) {
            //ShowInvalidInputMsg
            showInvalidInputMsg("Name cannot be empty")
            return
        }

        if (contactPhone.isBlank()) {
            //ShowInvalidInputMsg
            showInvalidInputMsg("Phone Number cannot be empty")
            return
        }

        if (contact != null) {
            //updateContact
            val updatedContact =
                contact.copy(name = contactName, phone = contactPhone, image = contactImage)
            updateContact(updatedContact)
        } else {
            val createdContact =
                Contact(name = contactName, phone = contactPhone, image = contactImage)
            createContact(createdContact)
        }
    }

    private fun showInvalidInputMsg(s: String) = viewModelScope.launch {
        addEditContactEventChannel.send(AddEditContactEvent.ShowInvalidInputMessage(s))
    }

    private fun createContact(createdContact: Contact) = viewModelScope.launch {
        contactRepository.insertContact(createdContact)
        addEditContactEventChannel.send(
            AddEditContactEvent.NavigateBackWithResult(
                ADD_TASK_RESULT_OK
            )
        )
    }

    private fun updateContact(updatedContact: Contact) = viewModelScope.launch {
        contactRepository.updateContact(updatedContact)
        addEditContactEventChannel.send(
            AddEditContactEvent.NavigateBackWithResult(
                EDIT_TASK_RESULT_OK
            )
        )
    }

    sealed class AddEditContactEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditContactEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditContactEvent()
    }

}