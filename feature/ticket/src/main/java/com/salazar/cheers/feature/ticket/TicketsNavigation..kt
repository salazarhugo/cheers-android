package com.salazar.cheers.feature.ticket

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants

const val ticketsNavigationRoute = "tickets_route"
private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/tickets"

fun NavController.navigateToTickets(navOptions: NavOptions? = null) {
    this.navigate(ticketsNavigationRoute, navOptions)
}

fun NavGraphBuilder.ticketsScreen(
    navigateBack: () -> Unit,
    navigateToTicketDetails: (String) -> Unit,
) {
    composable(
        route = ticketsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        TicketsRoute(
            navigateBack = navigateBack,
            navigateToTicketDetails = navigateToTicketDetails,
        )
    }
}
