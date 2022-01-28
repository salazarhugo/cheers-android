package com.salazar.cheers.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.navigation.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Settings screen.
 *
 * @param settingsViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel,
    navActions: CheersNavigationActions,
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val context = LocalContext.current

    SettingsScreen(
        uiState = uiState,
        onBackPressed = { navActions.navigateToProfile() },
        onSignOut = {
            FirebaseAuth.getInstance().signOut()
            navActions.navigateToSignIn()
//            context.startActivity(Intent(context, SignInActivity::class.java).newTask().clearTask())
        }
    )
}


