package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.UsersRepository

@Serializable
@JvmInline
value class UserId(val int: Int)

fun UsersRepository.UserId.serializable(): UserId = UserId(int)