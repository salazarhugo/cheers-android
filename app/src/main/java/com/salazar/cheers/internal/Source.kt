package com.salazar.cheers.internal


//@Entity(tableName = "payment")
data class Source(
    val id: String? = null,
    val `object`: String? = null,
    val address_city: String? = null,
    val address_country: String? = null,
    val address_line1: String? = null,
    val address_line1_check: String? = null,
    val address_line2: String? = null,
    val address_state: String? = null,
    val address_zip: String? = null,
    val address_zip_check: String? = null,
    val brand: String? = null,
    val country: String? = null,
    val customer: String? = null,
    val cvc_check: String? = null,
    val dynamic_last4: String? = null,
    val exp_month: Int = 0,
    val exp_year: Int = 0,
    val fingerprint: String? = null,
    val funding: String? = null,
    val last4: String? = null,
    val name: String? = null,
    val tokenization_method: String? = null
)
