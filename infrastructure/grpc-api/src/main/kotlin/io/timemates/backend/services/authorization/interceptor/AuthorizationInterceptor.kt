package io.timemates.backend.services.authorization.interceptor

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.kotlin.CoroutineContextServerInterceptor
import io.grpc.kotlin.GrpcContextElement
import io.timemates.backend.services.authorization.provider.AuthorizationProvider
import kotlin.coroutines.CoroutineContext

class AuthorizationInterceptor(
    private val authorizationProvider: AuthorizationProvider,
) : CoroutineContextServerInterceptor() {
    companion object {
        private val ACCESS_TOKEN_METADATA_KEY: Metadata.Key<String> =
            Metadata.Key.of("access-token", Metadata.ASCII_STRING_MARSHALLER)
    }

    override fun coroutineContext(call: ServerCall<*, *>, headers: Metadata): CoroutineContext {
        return AuthorizationContext(headers.get(ACCESS_TOKEN_METADATA_KEY), authorizationProvider)
    }
}