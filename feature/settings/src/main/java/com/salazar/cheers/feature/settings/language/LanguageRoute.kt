package com.salazar.cheers.feature.settings.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.feature.settings.SettingsViewModel

@Composable
fun LanguageRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LanguageScreen(
        onBackPressed = navigateBack,
        onLanguageChange = settingsViewModel::persistLanguage,
        language = uiState.language,
    )
}

