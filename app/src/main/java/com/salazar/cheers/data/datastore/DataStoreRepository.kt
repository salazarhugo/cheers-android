package com.salazar.cheers.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.salazar.cheers.ChatCounter
import com.salazar.cheers.Settings
import com.salazar.cheers.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import javax.inject.Inject


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreRepository @Inject constructor(
    private val settingsStore: DataStore<Settings>
) {

    private val TAG: String = "UserPreferencesRepo"

    val userPreferencesFlow: Flow<Settings> = settingsStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(Settings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateTheme(theme: Theme) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setTheme(theme).build()
        }
    }

    suspend fun resetChatCounter() {
//        settingsStore.updateData { currentPreferences ->
//            val chatCounter = currentPreferences.chatCounter
//            chatCounter.toBuilder().unreadChatCounter = chatCounter.unreadChatCounter + 1
//            currentPreferences.toBuilder().setChatCounter(chatCounter).build()
//        }
    }
}