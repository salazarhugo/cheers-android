package com.salazar.cheers.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    presentPaymentSheet: (String) -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsState()


    fun callCreatePaymentIntent(amount: Int) {
        val data = hashMapOf(
            "amount" to amount,
            "currency" to "eur"
        )
        val paymentDocument = Firebase.firestore.collection("stripe_customers")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .collection("payments")
            .document()

        paymentDocument.set(data)
        paymentDocument.addSnapshotListener { value, error ->
            if (value == null) return@addSnapshotListener
            if (value.data == null || value.data?.get("client_secret") == null) return@addSnapshotListener
            val clientSecret = value.data?.get("client_secret") as String;
            presentPaymentSheet(clientSecret)
        }
    }

    SettingsScreen(
        uiState = uiState,
        onBackPressed = { navActions.navigateToProfile() },
        onSignOut = {
            FirebaseAuth.getInstance().signOut()
            navActions.navigateToSignIn()
        },
        navigateToTheme = { navActions.navigateToTheme() },
        navigateToNotifications = { navActions.navigateToNotifications() },
        navigateToLanguage = { navActions.navigateToLanguage() },
        navigateToAddPaymentMethod = { navActions.navigateToAddPaymentMethod() },
        navigateToPaymentHistory = { navActions.navigateToPaymentHistory() },
        navigateToBecomeVip = { callCreatePaymentIntent(49999)},
        navigateToRecharge = { navActions.navigateToRecharge() }
    )
}


