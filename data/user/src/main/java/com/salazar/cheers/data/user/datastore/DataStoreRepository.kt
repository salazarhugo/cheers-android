package com.salazar.cheers.data.user.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.salazar.cheers.Language
import com.salazar.cheers.Settings
import com.salazar.cheers.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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
                Log.e(TAG, "Error reading user preferences.", exception)
                emit(Settings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    fun getSelectedHomeTab(): Flow<Int> {
        return userPreferencesFlow.map { it.selectedHomeTab }
    }

    fun getCity(): Flow<String> {
        return userPreferencesFlow.map { it.city }
    }

    fun getLocationEnabled(): Flow<Boolean> {
        return userPreferencesFlow.map { it.autoLocationEnabled }
    }

    fun getBiometricEnabled(): Flow<Boolean> {
        return userPreferencesFlow.map { it.hasBiometric }
    }

    fun getPasscode(): Flow<String> {
        return userPreferencesFlow.map { it.passcode }
    }

    fun getUsername(): Flow<String> {
        return userPreferencesFlow.map { it.username }
    }

    suspend fun updateSelectedHomeTab(selectedHomeTab: Int) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setSelectedHomeTab(selectedHomeTab).build()
        }
    }

    suspend fun updateCity(city: String) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setCity(city).build()
        }
    }

    suspend fun updateLocationEnabled(autoLocationEnabled: Boolean) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setAutoLocationEnabled(autoLocationEnabled).build()
        }
    }

    suspend fun updateUsername(username: String) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setUsername(username).build()
        }
    }

    suspend fun updatePasscode(passcode: String) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setPasscode(passcode).build()
            currentPreferences.toBuilder().setPasscodeEnabled(passcode.isNotBlank()).build()
        }
    }

    suspend fun updateHideContent(hideContent: Boolean) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setHideContent(hideContent).build()
        }
    }

    suspend fun toggleHideContent() {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setHideContent(!currentPreferences.hideContent).build()
        }
    }

    suspend fun toggleBiometric() {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setHasBiometric(!currentPreferences.hasBiometric).build()
        }
    }

    suspend fun updateTheme(theme: Theme) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setTheme(theme).build()
        }
    }

    suspend fun updateLanguage(theme: Language) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setLanguage(theme).build()
        }
    }

    suspend fun updateGhostMode(ghostMode: Boolean) {
        settingsStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setGhostMode(ghostMode).build()
        }
    }
}