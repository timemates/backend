package io.timemates.backend.authorization.types.metadata.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.reflection.wrapperTypeName

@JvmInline
value class ClientVersion private constructor(val string: String) {
    companion object : SafeConstructor<ClientVersion, String>() {
        override val displayName: String by wrapperTypeName()

        context(ValidationFailureHandler)
        override fun create(value: String): ClientVersion {
            return when (value.length) {
                0 -> onFail(FailureMessage.ofBlank())
                else -> ClientVersion(value)
            }
        }
    }
}