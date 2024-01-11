package com.salazar.cheers.core.data.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.salazar.cheers.data.user.datastore.DataStoreRepository
import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.ClientCall
import io.grpc.ClientInterceptor
import io.grpc.ClientInterceptors.CheckedForwardingClientCall
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.grpc.MethodDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Request
import okhttp3.internal.format
import java.lang.String
import javax.inject.Inject


class TokenInterceptor @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
): ClientInterceptor {
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
                    dataStoreRepository.getIdToken().first()
                }

                if (idToken.isNotBlank()) {
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