package com.salazar.cheers.data.mapper

import cheers.party.v1.PartyItem
import cheers.party.v1.PartyResponse
import com.salazar.cheers.internal.Party
import com.salazar.cheers.internal.Privacy

fun PartyItem.toParty(accountId: String): Party {
 return Party().copy(
     id = party.id,
     name = party.name,
     description = party.description,
     startDate  = party.startDate.seconds * 1000,
     endDate = party.endDate.seconds * 1000,
     created = party.createTime.seconds * 1000,
     hostId = party.hostId,
     hostName = user.name,
     price = 0,
     participants = emptyList(),
     showGuestList = false,
     showOnMap = false,
     interested = false,
     interestedCount = interestedCount.toInt(),
     going = false,
     goingCount = goingCount.toInt(),
     bannerUrl = party.bannerUrl,
     address = party.address,
     mutualProfilePictureUrls= emptyList(),
     mutualUsernames= emptyList(),
     mutualCount = 0,
     locationName = party.locationName,
     latitude= party.latitude,
     longitude= party.longitude,
     privacy  = Privacy.PUBLIC,
     accountId = accountId,
    )
}