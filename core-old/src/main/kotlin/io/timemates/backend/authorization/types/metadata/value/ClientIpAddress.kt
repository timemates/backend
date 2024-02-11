package io.timemates.backend.authorization.types.metadata.value

import io.timemates.backend.validation.FailureMessage
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class ClientIpAddress private constructor(val string: String) {
    companion object : SafeConstructor<ClientIpAddress, String>() {
        override val displayName by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: String): ClientIpAddress {
            return when (value.length) {
                0 -> onFail(io.timemates.backend.validation.FailureMessage.ofBlank())
                else -> ClientIpAddress(value)
            }
        }
    }
}