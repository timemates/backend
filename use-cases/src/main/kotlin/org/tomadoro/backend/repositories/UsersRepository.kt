package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.UserName

interface UsersRepository {
    suspend fun createUser(userName: UserName, creationTime: DateTime): UserId
    suspend fun getUser(userId: UserId): User?

    class User(
        val userId: UserId,
        val userName: UserName
    )

    @JvmInline
    value class UserId(val int: Int)
}