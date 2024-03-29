package com.salazar.cheers.friendship.ui.manage_friendship

sealed class ManageFriendshipUIAction {
    data object OnReportClick : ManageFriendshipUIAction()
    data object OnBlockClick : ManageFriendshipUIAction()
    data object OnRemoveFriendClick : ManageFriendshipUIAction()
    data object OnDoneClick : ManageFriendshipUIAction()
}
