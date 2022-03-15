package com.salazar.cheers.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
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
    val uriHandler = LocalUriHandler.current

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
//            navActions.navigateToSignIn()
        },
        onSettingsUIAction = { action ->
            when (action) {
                is SettingsUIAction.OnThemeClick -> navActions.navigateToTheme()
                is SettingsUIAction.OnLanguageClick -> navActions.navigateToLanguage()
                is SettingsUIAction.OnRechargeClick -> navActions.navigateToRecharge()
                is SettingsUIAction.OnAddPaymentClick -> navActions.navigateToAddPaymentMethod()
                is SettingsUIAction.OnPaymentHistoryClick -> navActions.navigateToPaymentHistory()
                is SettingsUIAction.OnNotificationsClick -> navActions.navigateToNotifications()
                is SettingsUIAction.OnPrivacyPolicyClick -> uriHandler.openUri("https://cheers-a275e.web.app/privacy-policy")
                is SettingsUIAction.OnTermsOfUseClick -> uriHandler.openUri("https://cheers-a275e.web.app/terms-of-use")
            }
        },
        navigateToBecomeVip = { callCreatePaymentIntent(49999) },
    )
}

sealed class SettingsUIAction {
    object OnThemeClick : SettingsUIAction()
    object OnNotificationsClick : SettingsUIAction()
    object OnLanguageClick : SettingsUIAction()
    object OnAddPaymentClick : SettingsUIAction()
    object OnPaymentHistoryClick : SettingsUIAction()
    object OnRechargeClick : SettingsUIAction()
    object OnPrivacyPolicyClick : SettingsUIAction()
    object OnTermsOfUseClick : SettingsUIAction()
}
