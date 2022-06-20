package com.salazar.cheers.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.Theme
import com.salazar.cheers.data.datastore.DataStoreRepository
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.db.UserPreferenceDao
import com.salazar.cheers.data.entities.UserPreference
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SettingsUiState(
    val isLoading: Boolean = false,
    val signedOut: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
    val theme: Theme = Theme.SYSTEM_DEFAULT,
    val language: Language = Language.English,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private var cheersDao: CheersDao,
    private var preferenceDao: UserPreferenceDao,
    private val userRepository: UserRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            dataStoreRepository.userPreferencesFlow.collect { appSettings ->
                viewModelState.update {
                    it.copy(theme = appSettings.theme)
                }
            }
        }
    }

    fun onDeleteAccount() {
        FirebaseAuth.getInstance().currentUser!!.delete()
            .addOnSuccessListener {
                viewModelState.update {
                    it.copy(signedOut = true)
                }
            }
            .addOnFailureListener {
                updateMessage(it.message.toString())
            }
    }

    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            dataStoreRepository.updateTheme(theme)
        }
    }

    private fun updateMessage(message: String) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    fun persistLanguage(language: Language) {
        viewModelState.update {
            it.copy(language = language)
        }

//        val preference = UserPreference(FirebaseAuth.getInstance().currentUser?.uid!!, theme)
//        viewModelScope.launch {
//            preferenceDao.insert(preference)
//        }
    }
}

