package com.salazar.cheers.feature.create_note

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val createNoteNavigationRoute = "create_note_route"
private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/create_note"

fun NavController.navigateToCreateNote(
    navOptions: NavOptions? = null,
) {
    this.navigate(createNoteNavigationRoute, navOptions)
}

fun NavGraphBuilder.createNoteScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = createNoteNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        CreateNoteRoute(
            navigateBack = navigateBack,
        )
    }
}
