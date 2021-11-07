package com.asterisk.contactapp.data

import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val contactDao: ContactDao
) {


}