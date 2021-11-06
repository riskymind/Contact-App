package com.asterisk.contactapp.data

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "contact_table")
@Parcelize
data class Contact(
    val name: String,
    val phone: Long,
    val image: String,
    val favorite: Boolean,
    val created: Long = System.currentTimeMillis()
) : Parcelable {
    val createdFormattedDate: String
        get() = DateFormat.getDateTimeInstance().format(created)
}