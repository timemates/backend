package io.timemates.backend.services.authorization.interceptor

import io.grpc.Grpc
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.kotlin.CoroutineContextServerInterceptor
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.services.common.validation.createOrStatus
import kotlin.coroutines.CoroutineContext

class IpAddressInterceptor : CoroutineContextServerInterceptor() {
    override fun coroutineContext(call: ServerCall<*, *>, headers: Metadata): CoroutineContext {
        return SessionContext(
            ipAddress = ClientIpAddress.createOrStatus(call.attributes.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString()),
        )
    }
}