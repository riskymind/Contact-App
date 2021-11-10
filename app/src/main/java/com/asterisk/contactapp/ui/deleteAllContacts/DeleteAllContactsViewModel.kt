package com.asterisk.contactapp.ui.deleteAllContacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asterisk.contactapp.data.ContactRepository
import com.asterisk.contactapp.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        contactRepository.deleteAllContacts()
    }
}