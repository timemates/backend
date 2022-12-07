package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.domain.DateTime

@Serializable
@JvmInline
value class Milliseconds(val long: Long)

fun org.tomadoro.backend.domain.Milliseconds.serializable() = Milliseconds(long)
fun DateTime.serializable() = Milliseconds(long)

fun Milliseconds.internal() = org.tomadoro.backend.domain.Milliseconds(long)