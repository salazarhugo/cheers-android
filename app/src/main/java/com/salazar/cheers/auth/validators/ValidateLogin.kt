package com.salazar.cheers.auth.validators


class ValidateLogin {
    fun execute(login: String): ValidationResult {
        if (login.isBlank())
            return ValidationResult(
                successful = false,
                errorTitle = "Missing Field",
                errorMessage = "The login field can't be blank"
            )

        return ValidationResult(successful = true)
    }
}