package com.salazar.cheers.auth.validators

data class ValidationResult(
    val successful: Boolean,
    val errorTitle: String = "",
    val errorMessage: String = "",
)