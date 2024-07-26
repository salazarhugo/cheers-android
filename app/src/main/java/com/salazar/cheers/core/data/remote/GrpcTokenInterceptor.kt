package com.salazar.cheers.core.data.remote

import com.salazar.cheers.domain.get_id_token.GetIdTokenUseCase
import com.salazar.cheers.shared.util.result.getOrNull
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ClientInterceptors.CheckedForwardingClientCall
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.grpc.MethodDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.internal.format
import javax.inject.Inject


class TokenInterceptor @Inject constructor(
    private val getIdTokenUseCase: GetIdTokenUseCase,
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
                headers: Metadata,
            ) {
                val idToken = runBlocking(Dispatchers.IO) {
                    getIdTokenUseCase().getOrNull()
                }

                if (!idToken.isNullOrBlank()) {
                    headers.put(
                        Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER),
                        format("Bearer %s", idToken)
                    )
                }
                delegate().start(responseListener, headers)
            }
        }
    }
}