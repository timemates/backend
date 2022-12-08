package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.ShortBio
import org.tomadoro.backend.domain.UserName

class MockedUsersRepository : UsersRepository {
    private val users: MutableList<UsersRepository.User> = mutableListOf()

    override suspend fun createUser(
        userName: UserName,
        shortBio: ShortBio?,
        creationTime: DateTime
    ): UsersRepository.UserId {
        users += UsersRepository.User(UsersRepository.UserId(users.size), userName, shortBio, null)
        return UsersRepository.UserId(users.lastIndex)
    }

    override suspend fun getUser(userId: UsersRepository.UserId): UsersRepository.User? {
        return users.firstOrNull { it.userId == userId }
    }

    override suspend fun getUsers(collection: Collection<UsersRepository.UserId>): Collection<UsersRepository.User> {
        return users.filter { it.userId in collection }
    }

    override suspend fun edit(userId: UsersRepository.UserId, patch: UsersRepository.User.Patch) {
        val index = users.indexOfFirst { it.userId == userId }
        val user = users[index]
        users[index] = user.copy(
            name = patch.name ?: user.name,
            shortBio = patch.shortBio ?: user.shortBio,
            avatarFileId = patch.avatarFileId ?: user.avatarFileId
        )
    }
}