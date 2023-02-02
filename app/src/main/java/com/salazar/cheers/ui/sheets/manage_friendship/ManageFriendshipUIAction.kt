package com.salazar.cheers.ui.sheets.manage_friendship

sealed class ManageFriendshipUIAction {
    object OnReportClick : ManageFriendshipUIAction()
    object OnBlockClick : ManageFriendshipUIAction()
    object OnRemoveFriendClick : ManageFriendshipUIAction()
    object OnDoneClick : ManageFriendshipUIAction()
}
