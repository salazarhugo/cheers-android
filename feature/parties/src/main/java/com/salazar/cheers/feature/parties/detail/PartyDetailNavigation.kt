package com.salazar.cheers.feature.parties.detail

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink

const val PARTY_ID = "partyID"
const val partyDetailNavigationRoute = "party_route/{$PARTY_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://maparty.app/party/{$PARTY_ID}"

fun NavController.navigateToPartyDetail(
    partyID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        "party_route/$partyID",
        navOptions,
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
    composable(
        route = partyDetailNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
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
