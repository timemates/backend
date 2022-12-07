package org.tomadoro.backend.domain


import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@JvmInline
value class Milliseconds(val long: Long)

fun Milliseconds.toDuration() = long.milliseconds
fun Duration.toMilliseconds() = Milliseconds(inWholeMilliseconds)