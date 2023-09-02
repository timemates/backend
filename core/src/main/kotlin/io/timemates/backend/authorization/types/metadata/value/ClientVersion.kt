package io.timemates.backend.authorization.types.metadata.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class ClientVersion private constructor(val double: Double) {
    companion object : SafeConstructor<ClientVersion, Double>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: Double): ClientVersion {
            return when {
                value < 1 -> onFail(FailureMessage.ofMin(1))
                else -> ClientVersion(value)
            }
        }
    }
}