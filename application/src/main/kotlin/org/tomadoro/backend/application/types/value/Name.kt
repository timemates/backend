package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.domain.value.UserName

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