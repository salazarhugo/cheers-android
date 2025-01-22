package com.salazar.cheers.ui.main.party.edit

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.salazar.cheers.core.util.Constants

const val PARTY_ID = "party_id"
const val editPartyNavigationRoute = "edit_party_route?$PARTY_ID={$PARTY_ID}"

private const val DEEP_LINK_URI_PATTERN = "${Constants.DEEPLINK_BASE_URL}/party/{party_id}/edit"

fun NavController.navigateToEditParty(
    partyID: String,
    navOptions: NavOptions? = null,
) {
    this.navigate("edit_party_route?$PARTY_ID=$partyID", navOptions)
}

fun NavGraphBuilder.editPartyScreen(
    navigateBack: () -> Unit,
    navigateToOtherProfile: (String) -> Unit,
    navigateToRoomDetails: (String) -> Unit,
) {
//        EditEventRoute(
//            navigateBack = navigateBack,
//        )
}
