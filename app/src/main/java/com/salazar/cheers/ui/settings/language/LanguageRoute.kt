package com.salazar.cheers.ui.settings.language

import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
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
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LanguageScreen(
        onBackPressed = { navActions.navigateBack() },
        onLanguageChange = settingsViewModel::persistLanguage,
        language = uiState.language,
    )
}

