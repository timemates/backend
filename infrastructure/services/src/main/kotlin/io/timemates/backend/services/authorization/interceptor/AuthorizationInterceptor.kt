package io.timemates.backend.services.authorization.interceptor

import io.grpc.Context
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.kotlin.CoroutineContextServerInterceptor
import io.grpc.kotlin.GrpcContextElement
import io.timemates.backend.services.authorization.provider.AuthorizationProvider
import kotlin.coroutines.CoroutineContext

class AuthorizationInterceptor(
    private val authorizationProvider: AuthorizationProvider
) : CoroutineContextServerInterceptor() {
    companion object {
        private val ACCESS_TOKEN_METADATA_KEY: Metadata.Key<String> =
            Metadata.Key.of("access-token", Metadata.ASCII_STRING_MARSHALLER)

        private val OMIT_AUTHORIZATION: Metadata.Key<Boolean?> =
            Metadata.Key.of(
                "omit_authorization",
                object : Metadata.BinaryMarshaller<Boolean> {
                    override fun toBytes(value: Boolean): ByteArray {
                        return byteArrayOf(if (value) 1 else 0)
                    }

                    override fun parseBytes(serialized: ByteArray): Boolean {
                        return serialized[0] == 1.toByte()
                    }
                }
            )

        val ACCESS_TOKEN_KEY: Context.Key<String> =
            Context.key("access-token")

        val AUTHORIZATION_PROVIDER = Context.key<AuthorizationProvider>("authorization-provider")
    }
    override fun coroutineContext(call: ServerCall<*, *>, headers: Metadata): CoroutineContext {
        val bypassAuth = headers.get(OMIT_AUTHORIZATION) ?: false

        return if (!bypassAuth)
            GrpcContextElement(
                Context.current()
                    .withValue(ACCESS_TOKEN_KEY, headers.get(ACCESS_TOKEN_METADATA_KEY))
                    .withValue(AUTHORIZATION_PROVIDER, authorizationProvider)
            )
        else GrpcContextElement(Context.current())
    }
}