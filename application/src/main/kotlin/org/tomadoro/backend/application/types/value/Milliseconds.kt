package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.domain.value.DateTime

@Serializable
@JvmInline
value class Milliseconds(val long: Long)

fun org.tomadoro.backend.domain.value.Milliseconds.serializable() = Milliseconds(long)
fun DateTime.serializable() = Milliseconds(long)

fun Milliseconds.internal() = org.tomadoro.backend.domain.value.Milliseconds(long)