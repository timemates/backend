package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.UserName

class MockedUsersRepository : UsersRepository {
    private val users: MutableList<UsersRepository.User> = mutableListOf()

    override suspend fun createUser(userName: UserName, creationTime: DateTime): UsersRepository.UserId {
        users += UsersRepository.User(UsersRepository.UserId(users.size), userName)
        return UsersRepository.UserId(users.lastIndex)
    }

    override suspend fun getUser(userId: UsersRepository.UserId): UsersRepository.User? {
        return UsersRepository.User(
            userId,
            users.getOrNull(userId.int)?.userName
                ?: return null
        )
    }
}