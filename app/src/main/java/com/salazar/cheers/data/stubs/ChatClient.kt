package com.salazar.cheers.data.stubs

import io.grpc.ManagedChannel
import java.io.Closeable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PartyClient @Inject constructor(
    private val channel: ManagedChannel
) : Closeable {

    private val stub: PartyService.ChatServiceCoroutineStub =
        PartyServiceKt.ChatServiceCoroutineStub(channel)

    suspend fun greet(name: String) {
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}
