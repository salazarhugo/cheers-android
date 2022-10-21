package com.salazar.cheers.data.stubs

import com.salazar.cheers.*
import kotlinx.coroutines.flow.Flow


class ChatService : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {

    override fun joinRoom(request: RoomId): Flow<Message> {
        return joinRoom(request)
    }

    override suspend fun sendMessage(requests: Flow<Message>): MessageAck {
        return sendMessage(requests = requests)
    }

    override suspend fun getRoomId(request: GetRoomIdReq): RoomId {
        return getRoomId(request = request)
    }

    override fun getRooms(request: Empty): Flow<Room> {
        return getRooms(request = request)
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

    override suspend fun createChat(request: CreateChatReq): Room {
        return createChat(request)
    }

    override suspend fun deleteRoom(request: RoomId): Empty {
        return deleteRoom(request)
    }

    override suspend fun leaveRoom(request: RoomId): Empty {
        return leaveRoom(request)
    }
}