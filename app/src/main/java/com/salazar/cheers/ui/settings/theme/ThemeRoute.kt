package com.salazar.cheers.ui.settings.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.settings.SettingsViewModel

/**
 * Stateful composable that displays the Navigation route for the Theme screen.
 *
 * @param themeViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun ThemeRoute(
    navActions: CheersNavigationActions,
    settingsViewModel: SettingsViewModel,
) {
    val uiState by settingsViewModel.uiState.collectAsState()

    ThemeScreen(
        onBackPressed = { navActions.navigateBack() },
        theme = uiState.theme,
        onThemeChange = settingsViewModel::persistTheme,
    )
}


