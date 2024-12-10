package com.salazar.cheers.feature.home.party_feed

import androidx.compose.runtime.Composable
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.ui.item.party.PartyList
import com.salazar.cheers.core.ui.ui.LoadingScreen

@Composable
fun PartyFeedScreen(
    parties: List<Party>?,
    onPartyClicked: (String) -> Unit,
    onMoreClick: (String) -> Unit,
    onChangeCityClick: () -> Unit,
    onCreatePartyClick: () -> Unit,
) {
    if (parties == null) {
        LoadingScreen()
    } else {
        PartyList(
            events = parties,
            onPartyClicked = onPartyClicked,
            onMoreClick = onMoreClick,
            onChangeCityClick = onChangeCityClick,
            onCreatePartyClick = onCreatePartyClick,
        )
    }
}