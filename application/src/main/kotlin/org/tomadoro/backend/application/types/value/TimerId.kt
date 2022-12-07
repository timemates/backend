package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.TimersRepository

@Serializable
@JvmInline
value class TimerId(val int: Int)

fun TimersRepository.TimerId.serializable() = TimerId(int)
fun TimerId.internal() = TimersRepository.TimerId(int)