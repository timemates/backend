package org.timemates.backend.types.auth.metadata.value

import org.timemates.backend.validation.CreationFailure
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.reflection.wrapperTypeName

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