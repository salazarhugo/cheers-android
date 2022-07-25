package com.salazar.cheers.data.repository

import com.salazar.cheers.ChatServiceGrpcKt
import io.grpc.ManagedChannel
import java.io.Closeable
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatClient @Inject constructor(
    private val channel: ManagedChannel
) : Closeable {

    private val stub: ChatServiceGrpcKt.ChatServiceCoroutineStub =
        ChatServiceGrpcKt.ChatServiceCoroutineStub(channel)

    suspend fun greet(name: String) {
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}