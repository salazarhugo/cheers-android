package com.salazar.cheers.ui.settings.payments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.components.LoadingScreen
import com.salazar.cheers.navigation.CheersNavigationActions

@Composable
fun RechargeRoute(
    rechargeViewModel: RechargeViewModel,
    navActions: CheersNavigationActions,
    presentPaymentSheet: (String) -> Unit,
) {
    val uiState by rechargeViewModel.uiState.collectAsState()

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
            rechargeViewModel.updateIsLoading(false)
            presentPaymentSheet(clientSecret)
        }
    }

    val recharges = listOf(
        Recharge(36, 0.50f),
        Recharge(70, 1.19f),
        Recharge(350, 5.85f),
        Recharge(700, 11.39f),
        Recharge(1400, 23.25f),
        Recharge(3500, 58.09f),
        Recharge(7000, 116.15f),
        Recharge(17500, 290.35f),
    )


    if (uiState.isLoading)
        LoadingScreen()
    else
        RechargeScreen(
            onRecharge = {
                rechargeViewModel.updateIsLoading(true)
                callCreatePaymentIntent((it.price * 100).toInt())
            },
            recharges = recharges,
            onBackPressed = { navActions.navigateBack() },
            coins = uiState.coins
        )
}

