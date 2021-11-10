package com.asterisk.contactapp.data

import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {

    fun getContacts(searchQuery: String, sortOrder: SortOrder, hideFav: Boolean) =
        contactDao.getContacts(searchQuery, sortOrder, hideFav)

    suspend fun insertContact(contact: Contact) = contactDao.insert(contact)
    suspend fun deleteContact(contact: Contact) = contactDao.delete(contact)
    suspend fun updateContact(contact: Contact) = contactDao.update(contact)

    suspend fun deleteAllContacts() = contactDao.deleteAllContacts()

}