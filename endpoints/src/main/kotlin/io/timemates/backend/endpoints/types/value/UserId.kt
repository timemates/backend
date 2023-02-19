package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.repositories.UsersRepository

@Serializable
@JvmInline
value class UserId(val int: Int)

fun UsersRepository.UserId.serializable(): UserId = UserId(int)
fun UserId.internal() = UsersRepository.UserId(int)