package io.timemates.backend.types.auth.metadata.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class ClientName private constructor(val string: String) {
    companion object : SafeConstructor<ClientName, String> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: String): Result<ClientName> {
            return when (value.length) {
                0 -> Result.failure(CreationFailure.ofBlank())
                else -> Result.success(ClientName(value))
            }
        }
    }
}