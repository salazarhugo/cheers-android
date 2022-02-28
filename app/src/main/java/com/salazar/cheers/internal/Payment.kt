package com.salazar.cheers.internal

import com.stripe.android.model.PaymentIntent
import com.stripe.android.model.PaymentIntent.CaptureMethod
import com.stripe.android.model.PaymentIntent.ConfirmationMethod
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.StripeIntent


//@Entity(tableName = "payment")
data class Payment(
    val id: String? = "",
    val paymentMethodTypes: List<String> = emptyList(),
    val amount: Long? = 0L,
    val canceledAt: Long = 0L,
    val cancellationReason: PaymentIntent.CancellationReason? = null,
    val captureMethod: CaptureMethod = CaptureMethod.Automatic,
    val clientSecret: String? = "",
    val confirmationMethod: ConfirmationMethod = ConfirmationMethod.Automatic,
    val created: Long = 0L,
    val currency: String? = "",
    val description: String? = null,
    val isLiveMode: Boolean = false,
    val paymentMethod: PaymentMethod? = null,
    val paymentMethodId: String? = null,
    val receiptEmail: String? = null,
    val status: String = "",
    val setupFutureUsage: StripeIntent.Usage? = null,
    val lastPaymentError: PaymentIntent.Error? = null,
    val shipping: PaymentIntent.Shipping? = null,
) {
}