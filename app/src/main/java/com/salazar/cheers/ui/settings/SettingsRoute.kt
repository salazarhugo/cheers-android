package com.salazar.cheers.ui.settings

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.CheersNavigationActions
import com.salazar.cheers.SignInActivity
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask

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
            context.startActivity(Intent(context, SignInActivity::class.java).newTask().clearTask())
        }
    )
}


