package com.salazar.cheers.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.CheersViewModel
import com.salazar.cheers.Settings
import com.salazar.cheers.Theme
import com.salazar.cheers.navigation.CheersNavGraph
import com.salazar.cheers.ui.theme.CheersTheme


@Composable
fun CheersApp(
    showInterstitialAd: () -> Unit,
    appSettings: Settings,
//    appState: CheersAppState = rememberCheersAppState()
) {
    val cheersViewModel = hiltViewModel<CheersViewModel>()
    val uiState by cheersViewModel.uiState.collectAsState()

    val darkTheme = isDarkTheme(appSettings.theme, isSystemInDarkTheme())
    val a = rememberModalBottomSheetState(ModalBottomSheetValue.Expanded)

    SetStatusBars(darkTheme = darkTheme)
    Text( text = "")

//    CheersTheme(darkTheme = darkTheme) {
//        CheersNavGraph(
//            uiState = uiState,
//            showInterstitialAd = showInterstitialAd,
//            appState = appState,
//        )
//    }
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