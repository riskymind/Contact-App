package com.asterisk.contactapp.data

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.asterisk.contactapp.data.PreferencesManager.PreferencesKeys.HIDE_FAVOURITE
import com.asterisk.contactapp.data.PreferencesManager.PreferencesKeys.SORT_ORDER
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


enum class SortOrder {
    BY_NAME, BY_DATE
}

private const val USER_PREFERENCES = "user_preferences"

data class FilterPreferences(val sortOrder: SortOrder, val hideFav: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore(USER_PREFERENCES)

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[SORT_ORDER] ?: SortOrder.BY_DATE.name
            )

            val hideFav = preferences[HIDE_FAVOURITE] ?: false
            FilterPreferences(sortOrder, hideFav)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideFav(hideFav: Boolean) {
        dataStore.edit { preferences ->
            preferences[HIDE_FAVOURITE] = hideFav
        }
    }


    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_FAVOURITE = preferencesKey<Boolean>("hide_fav")
    }
}