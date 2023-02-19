package io.timemates.backend.endpoints.types.value

import io.timemates.backend.types.value.UnixTime
import kotlinx.serialization.Serializable
import io.timemates.backend.types.value.Milliseconds as InternalMilliseconds

@Serializable
@JvmInline
value class Milliseconds(val long: Long)

fun InternalMilliseconds.serializable() = Milliseconds(long)
fun UnixTime.serializable() = Milliseconds(long)

fun Milliseconds.internal() = InternalMilliseconds(long)