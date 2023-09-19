package com.salazar.cheers.ui

import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.salazar.cheers.Language
import com.salazar.cheers.Settings
import com.salazar.cheers.Theme
import com.salazar.cheers.core.share.ui.LoadingScreen
import com.salazar.cheers.core.ui.CheersUiState
import com.salazar.cheers.core.ui.CheersViewModel
import com.salazar.cheers.navigation.CheersNavGraph
import com.salazar.cheers.core.ui.theme.CheersTheme
import com.salazar.cheers.core.util.Utils.setLocale
import com.salazar.common.util.LocalActivity


@Composable
fun CheersApp(
    appState: CheersAppState = rememberCheersAppState()
) {
    val cheersViewModel = hiltViewModel<CheersViewModel>()
    val uiState: CheersUiState by cheersViewModel.uiState.collectAsStateWithLifecycle()

    when(uiState) {
        is CheersUiState.Loading -> LoadingScreen()
        is CheersUiState.Initialized -> {
            val uiState = (uiState as CheersUiState.Initialized)
            val settings = uiState.settings
            val darkTheme = isDarkTheme(uiState.settings.theme, isSystemInDarkTheme())

            SetStatusBars(
                darkTheme = darkTheme,
            )
            SetLanguage(
                language = settings.language,
            )
            SetFlags(
                hideContent = settings.hideContent,
            )

            CheersTheme(darkTheme = darkTheme) {
                CheersNavGraph(
                    uiState = uiState,
                    appState = appState,
                )
            }
        }
    }
}

fun isDarkTheme(
    theme: Theme?,
    isSystemInDarkTheme: Boolean
): Boolean {
    return when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme
        Theme.UNRECOGNIZED -> isSystemInDarkTheme
        null -> true
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
@Composable
fun SetLanguage(language: Language) {
    val activity = LocalActivity.current

    val locale = when (language) {
        Language.FRENCH -> "fr"
        else -> "en"
    }

    activity.setLocale(locale)
}

@Composable
fun SetFlags(hideContent: Boolean) {
    val activity = LocalActivity.current

    LaunchedEffect(hideContent) {
        if (hideContent)
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        else
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}

