package com.salazar.cheers.data.stubs

import cheers.chat.v1.*
import kotlinx.coroutines.flow.Flow


class ChatService : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {

    override fun joinRoom(request: JoinRoomRequest): Flow<Message> {
        return super.joinRoom(request)
    }

    override suspend fun listMembers(request: ListMembersRequest): ListMembersResponse {
        return super.listMembers(request)
    }

    override suspend fun sendMessage(requests: Flow<Message>): SendMessageResponse {
        return super.sendMessage(requests)
    }

    override suspend fun getRoomId(request: GetRoomIdReq): RoomId {
        return getRoomId(request = request)
    }

    override suspend fun listRoom(request: ListRoomRequest): ListRoomResponse {
        return super.listRoom(request)
    }

    override suspend fun typingStart(request: TypingReq): Empty {
        return typingStart(request)
    }

    override suspend fun typingEnd(request: TypingReq): Empty {
        return typingEnd(request)
    }

    override suspend fun addToken(request: AddTokenReq): Empty {
        return addToken(request)
    }

    override suspend fun deleteRoom(request: RoomId): Empty {
        return deleteRoom(request)
    }

    override suspend fun leaveRoom(request: RoomId): Empty {
        return leaveRoom(request)
    }
}