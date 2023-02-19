package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.repositories.TimersRepository

@Serializable
@JvmInline
value class TimerId(val int: Int)

fun TimersRepository.TimerId.serializable() = TimerId(int)
fun TimerId.internal() = TimersRepository.TimerId(int)