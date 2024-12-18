package com.salazar.cheers.feature.settings.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.feature.settings.SettingsViewModel

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


