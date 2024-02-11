package io.timemates.backend.types.auth.metadata.value

import io.timemates.backend.validation.CreationFailure
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class ClientVersion private constructor(val double: Double) {
    companion object : SafeConstructor<ClientVersion, Double> {
        override val displayName: String by wrapperTypeName()

        override fun create(value: Double): Result<ClientVersion> {
            return when {
                value < 1 -> Result.failure(CreationFailure.ofMin(1))
                else -> Result.success(ClientVersion(value))
            }
        }
    }
}