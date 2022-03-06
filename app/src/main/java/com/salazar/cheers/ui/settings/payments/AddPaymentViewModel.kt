package com.salazar.cheers.ui.settings.payments

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.salazar.cheers.internal.Source
import com.salazar.cheers.util.FirestoreUtil
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.CardParams
import com.stripe.android.model.Token
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PaymentUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val sources: List<Source> = emptyList(),
)

@HiltViewModel
class AddPaymentViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(PaymentUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            FirestoreUtil.listenSources().collect { sources ->
                viewModelState.update {
                    it.copy(sources = sources, isLoading = false)
                }
            }
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun addCard(
        context: Context,
        cardParams: CardParams,
    ) {
        updateIsLoading(true)
        Stripe(
            context = context,
            publishableKey = ""
        ).createCardToken(cardParams = cardParams, callback = object : ApiResultCallback<Token> {
            override fun onSuccess(result: Token) {
                val tokenDoc = Firebase.firestore.collection("stripe_customers")
                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .collection("tokens")
                    .document()

                tokenDoc.set(hashMapOf("token" to result.id))
            }

            override fun onError(e: Exception) {
                Log.e("Stripe", e.toString())
            }
        })
    }
}

