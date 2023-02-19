package io.timemates.backend.endpoints.types.value

import io.timemates.backend.types.value.TimerName
import io.timemates.backend.types.value.UserName
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Name(val string: String) {
    init {
        require(string.length <= 50) {
            "Name length should not more than 50, but got ${string.length}"
        }
    }
}

fun UserName.serializable() = Name(string)
fun TimerName.serializable() = Name(string)

fun Name.internalAsUserName() = UserName(string)