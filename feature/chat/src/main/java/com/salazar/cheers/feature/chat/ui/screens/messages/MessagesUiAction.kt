import com.salazar.cheers.data.chat.models.ChatChannel

sealed class RoomsUIAction {
    data object OnBackPressed : RoomsUIAction()
    data object OnSwipeRefresh : RoomsUIAction()
    data class OnRoomClick(val roomId: String) : RoomsUIAction()
    data class OnCameraClick(val id: String) : RoomsUIAction()
    data class OnPinRoom(val roomId: String) : RoomsUIAction()
    data class OnRoomLongPress(val chat: ChatChannel) : RoomsUIAction()
    data class OnUserClick(val userId: String) : RoomsUIAction()
    data class OnSearchInputChange(val query: String) : RoomsUIAction()
}
