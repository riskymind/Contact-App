package com.asterisk.contactapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    fun getContacts(
        searchQuery: String,
        sortOrder: SortOrder,
        hideFav: Boolean
    ): Flow<List<Contact>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getContactsSortedByDate(searchQuery, hideFav)
            SortOrder.BY_NAME -> getContactsSortedByName(searchQuery, hideFav)
        }

    @Query("SELECT * FROM contact_table WHERE (favorite != :hideFav OR favorite = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY favorite DESC, name")
    fun getContactsSortedByName(searchQuery: String, hideFav: Boolean): Flow<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE (favorite != :hideFav OR favorite = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY favorite DESC, created")
    fun getContactsSortedByDate(searchQuery: String, hideFav: Boolean): Flow<List<Contact>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAllContacts()
}