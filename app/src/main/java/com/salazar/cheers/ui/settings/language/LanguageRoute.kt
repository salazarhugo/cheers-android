package com.salazar.cheers.ui.settings.language

import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.settings.SettingsViewModel

/**
 * Stateful composable that displays the Navigation route for the Language screen.
 *
 * @param languageViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun LanguageRoute(
    navActions: CheersNavigationActions,
    settingsViewModel: SettingsViewModel,
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    LanguageScreen(
        onBackPressed = { navActions.navigateBack() },
        onLanguageChange = settingsViewModel::persistLanguage,
        language = uiState.language,
    )
}

