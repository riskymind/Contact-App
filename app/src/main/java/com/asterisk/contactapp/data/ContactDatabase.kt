package com.asterisk.contactapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Contact::class], version = 1)
abstract class ContactDatabase : RoomDatabase() {

    abstract fun getContactDao(): ContactDao

    class InitialContactsAdded @Inject constructor(
        private val database: Provider<ContactDatabase>,
        @ApplicationContext private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().getContactDao()

            applicationScope.launch {
                dao.insert(Contact("Kelechi", "09029577152"))
                dao.insert(Contact("Asterisk", "09080547162", favorite = true))
                dao.insert(Contact("Zuri", "08068650020"))
                dao.insert(Contact("Nkemjika", "07045120705", favorite = true))
                dao.insert(Contact("Kamsi", "07046193975"))
            }
        }
    }
}