package com.salazar.cheers.feature.ticket.details

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.salazar.cheers.core.util.Constants

const val TICKET_ID = "ticketID"
const val ticketDetailsNavigationRoute = "ticket_route/{$TICKET_ID}"
private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/ticket/{${TICKET_ID}}"

fun NavController.navigateToTicketDetails(
    ticketID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        "ticket_route/$ticketID",
        navOptions,
    )
}

fun NavGraphBuilder.ticketDetailsScreen(
    navigateBack: () -> Unit,
) {
    composable(
        route = ticketDetailsNavigationRoute,
        deepLinks = listOf(
            navDeepLink { uriPattern = DEEP_LINK_URI_PATTERN },
        ),
    ) {
        TicketDetailsRoute(
            navigateBack = navigateBack,
        )
    }
}
