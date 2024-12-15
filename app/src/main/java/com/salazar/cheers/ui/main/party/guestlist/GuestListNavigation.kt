package com.salazar.cheers.ui.main.party.guestlist

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants
import kotlinx.serialization.Serializable


@Serializable
data class GuestListScreen(
    val partyID: String,
)

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/guestList"

fun NavController.navigateToGuestList(
    partyID: String,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = GuestListScreen(partyID = partyID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.guestListScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
) {
    composable<GuestListScreen>(
        deepLinks = listOf(
            navDeepLink<GuestListScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        GuestListRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
        )
    }
}
