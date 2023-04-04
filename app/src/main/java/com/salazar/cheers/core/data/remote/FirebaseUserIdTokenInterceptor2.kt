package com.salazar.cheers.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import io.grpc.*
import io.grpc.ClientInterceptors.CheckedForwardingClientCall
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.internal.format


class ErrorHandleInterceptor : ClientInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        return object :
            CheckedForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            override fun checkedStart(
                responseListener: Listener<RespT>?,
                headers: Metadata
            ) {
                runBlocking {
                    val result = FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()
                    result?.token?.let { token ->
                        headers.put(
                            Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER),
                            format("Bearer %s", token)
                        )
                    }
                }

                delegate().start(responseListener, headers)
            }
        }
    }
}