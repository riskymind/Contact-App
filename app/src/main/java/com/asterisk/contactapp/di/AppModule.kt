package com.asterisk.contactapp.di

import android.content.Context
import androidx.room.Room
import com.asterisk.contactapp.data.ContactDao
import com.asterisk.contactapp.data.ContactDatabase
import com.asterisk.contactapp.data.ContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContactDatabase(
        @ApplicationContext context: Context,
        callback: ContactDatabase.InitialContactsAdded
    ): ContactDatabase =
        Room.databaseBuilder(context, ContactDatabase::class.java, "contact_db")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    @Singleton
    fun provideContactDao(db: ContactDatabase): ContactDao = db.getContactDao()

    @Provides
    @Singleton
    fun provideContactRepository(contactDao: ContactDao): ContactRepository =
        ContactRepository(contactDao)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope