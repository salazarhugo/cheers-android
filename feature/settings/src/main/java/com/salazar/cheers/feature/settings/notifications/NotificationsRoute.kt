package com.salazar.cheers.feature.settings.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions
import com.salazar.cheers.feature.settings.SettingsViewModel

/**
 * Stateful composable that displays the Navigation route for the Notifications screen.
 *
 * @param settingsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun NotificationsRoute(
    navActions: CheersNavigationActions,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    NotificationsScreen(
        onBackPressed = { navActions.navigateBack() },
    )
}

