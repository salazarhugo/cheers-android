package com.salazar.cheers.feature.settings.language

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object LanguageScreen

fun NavController.navigateToLanguage(
    navOptions: NavOptions? = null,
) {
    this.navigate(LanguageScreen, navOptions)
}

fun NavGraphBuilder.languagesScreen(
    navigateBack: () -> Unit,
) {
    composable<LanguageScreen> {
        LanguageRoute(
            navigateBack = navigateBack,
        )
    }
}
