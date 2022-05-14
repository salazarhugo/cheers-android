package com.salazar.cheers.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.db.CheersDao
import com.salazar.cheers.data.db.UserPreferenceDao
import com.salazar.cheers.data.entities.Theme
import com.salazar.cheers.data.entities.UserPreference
import com.salazar.cheers.internal.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private var cheersDao: CheersDao,
    private var preferenceDao: UserPreferenceDao,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(SettingsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun persistTheme(theme: Theme) {
        viewModelState.update {
            it.copy(theme = theme)
        }

        val preference = UserPreference(FirebaseAuth.getInstance().currentUser?.uid!!, theme)
        viewModelScope.launch {
            preferenceDao.insert(preference)
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

data class SettingsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val searchInput: String = "",
    val theme: Theme = Theme.SYSTEM,
    val language: Language = Language.English,
)

