package com.salazar.cheers.feature.parties

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants

const val partiesNavigationRoute = "parties_route"
private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/parties"

fun NavController.navigateToParties(navOptions: NavOptions? = null) {
    this.navigate(partiesNavigationRoute, navOptions)
}

fun NavGraphBuilder.partiesScreen(
    navigateBack: () -> Unit,
    navigateToPartyDetail: (String) -> Unit,
    navigateToPartyMoreSheet: (String) -> Unit,
    navigateToTickets: () -> Unit,
    navigateToCreateParty: () -> Unit,
) {
    composable(
        route = partiesNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        PartiesRoute(
            navigateToPartyDetail = navigateToPartyDetail,
            navigateToPartyMoreSheet = navigateToPartyMoreSheet,
            navigateToTickets = navigateToTickets,
            navigateToCreateParty = navigateToCreateParty,
            onChangeCityClick = { TODO() },
        )
    }
}
