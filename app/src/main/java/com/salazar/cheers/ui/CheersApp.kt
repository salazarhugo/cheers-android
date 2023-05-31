package com.salazar.cheers.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.Settings
import com.salazar.cheers.Theme
import com.salazar.cheers.core.ui.CheersViewModel
import com.salazar.cheers.navigation.CheersNavGraph
import com.salazar.cheers.core.ui.theme.CheersTheme


@Composable
fun CheersApp(
    appSettings: Settings,
    appState: CheersAppState = rememberCheersAppState()
) {
    val cheersViewModel = hiltViewModel<CheersViewModel>()
    val uiState by cheersViewModel.uiState.collectAsStateWithLifecycle()

    val darkTheme = isDarkTheme(appSettings.theme, isSystemInDarkTheme())

    SetStatusBars(darkTheme = darkTheme)

    CheersTheme(darkTheme = darkTheme) {
        CheersNavGraph(
            uiState = uiState,
            appState = appState,
        )
    }
}

fun isDarkTheme(
    theme: Theme,
    isSystemInDarkTheme: Boolean
): Boolean {
    return when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme
        Theme.UNRECOGNIZED -> isSystemInDarkTheme
    }
}

@Composable
fun SetStatusBars(
    darkTheme: Boolean,
) {
    val systemUiController = rememberSystemUiController()
    val darkIcons = !darkTheme

//    val color = if (darkIcons) Color.White else Color(0xFF101010)

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = darkIcons,
        )
    }
}