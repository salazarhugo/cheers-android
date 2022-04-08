package com.salazar.cheers.backend

import android.content.Context
import io.grpc.ManagedChannelBuilder
import javax.inject.Inject


class rpcService @Inject constructor(
    private val appContext: Context
) {

    fun getUsers() {
        val managedChannel = ManagedChannelBuilder.forAddress("https://www.google.com", 443).build()
//        val blockingStub = ChatService.newBlockingStub(managedChannel)
//        val bookRequest = BookProto.GetBookRequest.newBuilder().setIsbn(9123471293487).build()
//        val book = blockingStub.getBook(bookRequest)
//        book.author
//        book.isbn
//        book.title
    }
}