package com.salazar.cheers.data.account

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.salazar.cheers.data.account.mapper.toAccount
import com.salazar.cheers.shared.data.response.LoginResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val localAccountDatastore = "localAccountDataStore"

/* Data store keys */
private val accountKey = stringPreferencesKey("accountKey")
private val idTokenKey = stringPreferencesKey("idTokenKey")

@Singleton
class LocalAccountDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) {

    private val Context.dataStore by preferencesDataStore(
        name = localAccountDatastore,
    )

    private val dataStore: DataStore<Preferences>
        get() = context.dataStore

    suspend fun getIdToken(): String? {
        return dataStore.data.map { prefs ->
            prefs[idTokenKey]
        }.firstOrNull()
    }

    suspend fun getAccount(): Account? {
        return getAccountFlow().firstOrNull()
    }

    fun getAccountFlow(): Flow<Account?> {
        return dataStore.data.map { prefs ->
            prefs[accountKey]?.let {
                runCatching {
                    gson.fromJson(it, Account::class.java)
                }.getOrNull()
            }
        }
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun putAccount(account: Account): Boolean {
        dataStore.edit { settings ->
            settings[accountKey] = gson.toJson(account)
        }
        return true
    }

    suspend fun putIdToken(idToken: String): Boolean {
        dataStore.edit { settings ->
            settings[idTokenKey] = idToken
        }
        return true
    }
}