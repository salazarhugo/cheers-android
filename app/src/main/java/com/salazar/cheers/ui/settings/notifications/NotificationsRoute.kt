package com.salazar.cheers.ui.settings.notifications

import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.salazar.cheers.navigation.CheersNavigationActions
import com.salazar.cheers.ui.settings.SettingsViewModel

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
    val uiState by settingsViewModel.uiState.collectAsState()

    NotificationsScreen(
        onBackPressed = { navActions.navigateBack() },
    )
}

