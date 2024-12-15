package com.salazar.cheers.feature.settings.security.passkeys

import com.salazar.cheers.core.model.Credential

data class PasskeysUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val signInMethods: List<String> = emptyList(),
    val passcodeEnabled: Boolean = false,
    val passkeys: List<Credential> = emptyList(),
)

