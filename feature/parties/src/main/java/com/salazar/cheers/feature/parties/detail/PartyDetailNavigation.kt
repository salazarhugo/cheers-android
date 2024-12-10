package com.salazar.cheers.feature.parties.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import kotlinx.serialization.Serializable

private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/party/{partyID}"

@Serializable
data class PartyDetailScreen(
    val partyID: String,
)

fun NavController.navigateToPartyDetail(
    partyID: String,
    navOptions: NavOptions? = null,
) {
    navigate(
        route = PartyDetailScreen(partyID = partyID),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.partyDetailScreen(
    navigateToMap: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToTicketing: (String) -> Unit,
    navigateToEditParty: (String) -> Unit,
    navigateToGuestList: (String) -> Unit,
) {
    composable<PartyDetailScreen>(
        deepLinks = listOf(
            navDeepLink<PartyDetailScreen>(basePath = DEEP_LINK_URI_PATTERN),
        ),
    ) {
        PartyDetailRoute(
            navigateBack = navigateBack,
            navigateToOtherProfile = navigateToOtherProfile,
            navigateToEditParty = navigateToEditParty,
            navigateToGuestList = navigateToGuestList,
            navigateToMap = navigateToMap,
            navigateToTicketing = navigateToTicketing,
        )
    }
}
