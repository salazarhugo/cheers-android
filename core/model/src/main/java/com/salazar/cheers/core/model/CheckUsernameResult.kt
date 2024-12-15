package com.salazar.cheers.core.model


data class CheckUsernameResult(
    val valid: Boolean,
    val invalidReason: String = "",
)