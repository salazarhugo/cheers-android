package com.salazar.cheers.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.salazar.cheers.data.datastore.DataStoreRepository.PreferenceKeys.name
import com.salazar.cheers.data.datastore.DataStoreRepository.PreferenceKeys.notificationCount
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepository @Inject constructor(
    @ApplicationContext context: Context,
) {

    object PreferenceKeys {
        val name = stringPreferencesKey("my_name")
        val notificationCount = intPreferencesKey("notification_count")
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun resetNotificationCount() {
        dataStore.edit { preference ->
            preference[notificationCount] = 0
        }
    }

    suspend fun incrementNotificationCount() {
        dataStore.edit { preference ->
            val current = preference[PreferenceKeys.notificationCount] ?: 0
            preference[PreferenceKeys.notificationCount] = current + 1
        }
    }

    val readFromDataStore: Flow<Int> = dataStore.data
        .catch { exception ->
            if(exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            }else {
                throw exception
            }
        }
        .map { preference ->
            val myName = preference[notificationCount] ?: 0
            myName
        }

}