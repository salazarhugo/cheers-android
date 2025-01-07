package com.salazar.cheers.ui.main.party.guestlist

import com.salazar.cheers.core.model.UserItem

sealed interface GuestListGoingState {
    data object Loading : GuestListGoingState
    data class Users(val users: List<UserItem>) :
        GuestListGoingState
}
