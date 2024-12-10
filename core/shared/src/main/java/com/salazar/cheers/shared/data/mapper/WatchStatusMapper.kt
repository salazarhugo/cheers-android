package com.salazar.cheers.shared.data.mapper

import com.salazar.cheers.core.model.WatchStatus
import cheers.party.v1.WatchStatus as WatchStatusPb


fun WatchStatusPb.toWatchStatus(): WatchStatus {
    return when(this) {
        WatchStatusPb.GOING -> WatchStatus.GOING
        WatchStatusPb.INTERESTED -> WatchStatus.INTERESTED
        WatchStatusPb.UNWATCHED -> WatchStatus.UNWATCHED
        WatchStatusPb.MAYBE -> WatchStatus.UNWATCHED
        WatchStatusPb.UNRECOGNIZED -> WatchStatus.UNWATCHED
    }
}

fun WatchStatus.toPartyAnswer(): WatchStatusPb {
    return when(this) {
        WatchStatus.INTERESTED -> WatchStatusPb.INTERESTED
        WatchStatus.GOING -> WatchStatusPb.GOING
        WatchStatus.UNWATCHED -> WatchStatusPb.UNWATCHED
    }
}
