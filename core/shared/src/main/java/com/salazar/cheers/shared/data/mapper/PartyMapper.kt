package com.salazar.cheers.shared.data.mapper

import cheers.party.v1.PartyItem
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.Privacy

fun PartyItem.toParty(): Party {
    return Party().copy(
        id = party.id,
        name = party.name,
        description = party.description,
        startDate = party.startDate,
        endDate = party.endDate,
        createTime = party.createTime,
        hostId = party.hostId,
        hostName = user.name,
        isHost = isCreator,
        watchStatus = viewerWatchStatus.toWatchStatus(),
        price = null,
        participants = emptyList(),
        showGuestList = false,
        showOnMap = false,
        interestedCount = interestedCount.toInt(),
        goingCount = goingCount.toInt(),
        bannerUrl = party.bannerUrl,
        address = party.address,
        city = party.city,
        locationName = party.locationName,
        latitude = party.latitude,
        longitude = party.longitude,
        privacy = Privacy.PUBLIC,
        mutualGoing = mutualGoingList.associate { it.picture to it.username },
    )
}