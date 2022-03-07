package com.salazar.cheers

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.data.entities.Theme
import com.salazar.cheers.navigation.CheersNavGraph
import com.salazar.cheers.ui.theme.CheersTheme

@Composable
fun CheersApp(
    presentPaymentSheet: (String) -> Unit,
) {

    val cheersViewModel = hiltViewModel<CheersViewModel>()
    val uiState by cheersViewModel.uiState.collectAsState()

    val preference = uiState.userPreference
    val darkTheme = when (preference.theme) {
        Theme.LIGHT -> false
        Theme.DARK -> true
        Theme.SYSTEM -> isSystemInDarkTheme()
    }

    SetStatusBars(darkTheme = darkTheme)

    CheersTheme(darkTheme = darkTheme) {
        ProvideWindowInsets {
            CheersNavGraph(
                darkTheme = darkTheme,
                presentPaymentSheet = presentPaymentSheet,
                user = uiState.user,
            )
        }
    }
}

@Composable
fun SetStatusBars(
    darkTheme: Boolean
) {
    val systemUiController = rememberSystemUiController()
    val darkIcons = !darkTheme

    SideEffect {
        systemUiController.setSystemBarsColor(
            if (darkIcons) Color.White else Color.Black,
            darkIcons = darkIcons
        )
    }
}
