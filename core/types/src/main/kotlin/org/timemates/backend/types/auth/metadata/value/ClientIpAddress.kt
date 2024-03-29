package org.timemates.backend.types.auth.metadata.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class ClientIpAddress private constructor(val string: String) {
    companion object : SafeConstructor<ClientIpAddress, String> {
        override val displayName by wrapperTypeName()

        override fun create(value: String): Result<ClientIpAddress> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                else -> Result.success(ClientIpAddress(value))
            }
        }
    }
}