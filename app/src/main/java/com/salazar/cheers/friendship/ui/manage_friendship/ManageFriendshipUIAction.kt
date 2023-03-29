package com.salazar.cheers.friendship.ui.manage_friendship

sealed class ManageFriendshipUIAction {
    object OnReportClick : ManageFriendshipUIAction()
    object OnBlockClick : ManageFriendshipUIAction()
    object OnRemoveFriendClick : ManageFriendshipUIAction()
    object OnDoneClick : ManageFriendshipUIAction()
}
