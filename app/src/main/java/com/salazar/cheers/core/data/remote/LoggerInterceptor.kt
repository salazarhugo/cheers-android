package com.salazar.cheers.core.data.remote

import android.util.Log
import com.salazar.cheers.BuildConfig
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ForwardingClientCall
import io.grpc.ForwardingClientCallListener
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import javax.inject.Inject


class LoggerInterceptor @Inject constructor(
): ClientInterceptor {
    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions,
        next: Channel
    ): ClientCall<ReqT, RespT> {
        val delegateCall = next.newCall(method, callOptions)

        return object : ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(delegateCall) {
            override fun sendMessage(message: ReqT) {
                if (BuildConfig.DEBUG) {
                    Log.i("LoggerInterceptor", "Calling: ${method.fullMethodName}")
                    Log.i("LoggerInterceptor", (message as com.google.protobuf.GeneratedMessageLite<*, *>).toString())
                }
                super.sendMessage(message)
            }

            override fun start(responseListener: Listener<RespT>?, headers: Metadata) {
                super.start(
                    object : ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(
                        responseListener
                    ) {
                        override fun onMessage(message: RespT) {
                            // This is where you can access and print the response body
                            if (BuildConfig.DEBUG) {
                                println("Response Body: $message")
                            }
                            super.onMessage(message)
                        }
                    },
                    headers
                )
            }
        }
    }
}
