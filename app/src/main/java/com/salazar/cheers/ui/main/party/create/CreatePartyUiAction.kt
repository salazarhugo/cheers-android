package com.salazar.cheers.ui.main.party.create

import com.salazar.cheers.core.model.Privacy


sealed class CreatePartyUIAction {
    data object OnShowMapChange : CreatePartyUIAction()
    data object OnDismiss : CreatePartyUIAction()
    data object OnAddPhoto : CreatePartyUIAction()
    data object OnPartyDetailsClick : CreatePartyUIAction()
    data object OnDescriptionClick : CreatePartyUIAction()
    data object OnBasicInfoClick : CreatePartyUIAction()
    data object OnLocationClick : CreatePartyUIAction()
    data object OnUploadParty : CreatePartyUIAction()
    data object OnAddPeopleClick : CreatePartyUIAction()
    data object OnHasEndDateToggle : CreatePartyUIAction()
    data class OnPrivacyChange(val privacy: Privacy) : CreatePartyUIAction()
    data class OnUserClick(val username: String) : CreatePartyUIAction()
}
