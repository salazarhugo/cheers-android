package com.salazar.cheers.ui.settings.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions
import com.salazar.cheers.ui.settings.SettingsViewModel

/**
 * Stateful composable that displays the Navigation route for the Theme screen.
 *
 * @param settingsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ThemeRoute(
    navActions: CheersNavigationActions,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    ThemeScreen(
        onBackPressed = { navActions.navigateBack() },
        theme = uiState.theme,
        onThemeChange = settingsViewModel::updateTheme,
    )
}


