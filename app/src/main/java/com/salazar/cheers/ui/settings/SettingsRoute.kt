package com.salazar.cheers.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Settings screen.
 *
 * @param searchViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    SettingsScreen(
        uiState = uiState,
        onBackPressed = { navActions.navigateToProfile() },
        onSignOut = {
            FirebaseAuth.getInstance().signOut()
//            startActivity(intentFor<SignInActivity>().newTask().clearTask())
        }
    )
}

