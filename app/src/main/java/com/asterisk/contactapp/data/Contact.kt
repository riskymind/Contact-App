package com.asterisk.contactapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat


@Entity(tableName = "contact_table")
@Parcelize
data class Contact(
    val name: String,
    val phone: String,
    val image: String = "",
    val favorite: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdFormattedDate: String
        get() = DateFormat.getDateTimeInstance().format(created)
}