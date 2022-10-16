package com.salazar.cheers.data.stubs

import cheers.party.v1.*

class PartyService : PartyServiceGrpcKt.PartyServiceCoroutineImplBase() {
    override suspend fun feedParty(request: FeedPartyRequest): FeedPartyResponse {
        return super.feedParty(request)
    }
}