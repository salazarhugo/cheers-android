package com.salazar.cheers.data.remote

import android.util.Log
import io.grpc.*
import io.grpc.ClientInterceptors.CheckedForwardingClientCall
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import okhttp3.internal.format


class ErrorHandleInterceptor(
    private val idToken: String,
) : ClientInterceptor {
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
                Log.d("HAHA", idToken)
                headers.put(
                    Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER),
                    format("Bearer %s", idToken)
                )

                delegate().start(responseListener, headers)
            }
        }
    }
}