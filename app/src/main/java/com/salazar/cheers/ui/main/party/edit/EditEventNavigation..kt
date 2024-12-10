package com.salazar.cheers.ui.main.party.edit

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions

const val PARTY_ID = "party_id"
const val editPartyNavigationRoute = "edit_party_route?$PARTY_ID={$PARTY_ID}"

private const val DEEP_LINK_URI_PATTERN = "https://maparty.app/party/$PARTY_ID/edit"

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
