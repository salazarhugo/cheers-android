package com.salazar.cheers.data.mapper

import cheers.party.v1.PartyItem
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.model.Privacy
import com.salazar.cheers.parties.data.mapper.toWatchStatus

fun PartyItem.toParty(accountId: String): Party {
 return Party().copy(
     id = party.id,
     name = party.name,
     description = party.description,
     startDate  = party.startDate,
     endDate = party.endDate,
     createTime = party.createTime,
     hostId = party.hostId,
     hostName = user.name,
     watchStatus = viewerWatchStatus.toWatchStatus(),
     price = party.minimumPrice.toInt(),
     participants = emptyList(),
     showGuestList = false,
     showOnMap = false,
     interestedCount = interestedCount.toInt(),
     goingCount = goingCount.toInt(),
     bannerUrl = party.bannerUrl,
     address = party.address,
     mutualProfilePictureUrls= mutualPicturesList,
     mutualUsernames= mutualUsernamesList,
     mutualCount = 2,
     locationName = party.locationName,
     latitude= party.latitude,
     longitude= party.longitude,
     privacy  = Privacy.PUBLIC,
     accountId = accountId,
    )
}