package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.domain.UserName

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