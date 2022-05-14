package com.salazar.cheers.backend

import com.salazar.cheers.*
import kotlinx.coroutines.flow.Flow


class ChatService constructor(
    private val client: ChatServiceGrpcKt.ChatServiceCoroutineStub,
) : ChatServiceGrpcKt.ChatServiceCoroutineImplBase() {

    override fun joinRoom(request: RoomId): Flow<Message> {
        return client.joinRoom(request)
    }

    override suspend fun sendMessage(requests: Flow<Message>): MessageAck {
        return client.sendMessage(requests = requests)
    }

    override suspend fun getRoomId(request: GetRoomIdReq): RoomId {
        return client.getRoomId(request = request)
    }

    override fun getRooms(request: Empty): Flow<Room> {
        return client.getRooms(request = request)
    }

    override suspend fun typingStart(request: TypingReq): Empty {
        return client.typingStart(request)
    }

    override suspend fun typingEnd(request: TypingReq): Empty {
        return client.typingEnd(request)
    }

    override suspend fun addToken(request: AddTokenReq): Empty {
        return client.addToken(request)
    }

    override suspend fun createChat(request: CreateChatReq): Room {
        return client.createChat(request)
    }

    override suspend fun deleteRoom(request: RoomId): Empty {
        return client.deleteRoom(request)
    }

    override suspend fun leaveRoom(request: RoomId): Empty {
        return client.leaveRoom(request)
    }
}
