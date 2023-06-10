package com.salazar.cheers.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.Language
import com.salazar.cheers.Theme
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.db.UserPreferenceDao
import com.salazar.cheers.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SettingsUiState(
    val isLoading: Boolean = false,
    val signedOut: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
    val theme: Theme = Theme.SYSTEM_DEFAULT,
    val language: Language = Language.ENGLISH,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private var cheersDao: CheersDao,
    private var preferenceDao: UserPreferenceDao,
    private val signOutUseCase: SignOutUseCase,
    private val dataStoreRepository: com.salazar.cheers.data.user.datastore.DataStoreRepository,
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
                    it.copy(
                        theme = appSettings.theme,
                        language = appSettings.language,
                    )
                }
            }
        }
    }

    fun onSignOut(onFinished: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            onFinished()
        }
    }

//    fun onDeleteAccount() {
//        FirebaseAuth.getInstance().currentUser!!.delete()
//            .addOnSuccessListener {
//                viewModelState.update {
//                    it.copy(signedOut = true)
//                }
//            }
//            .addOnFailureListener {
//                updateMessage(it.message.toString())
//            }
//    }
//
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
        viewModelScope.launch {
            dataStoreRepository.updateLanguage(language)
        }
    }
}

