package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UserName

class InMemoryUsersRepository : UsersRepository {
    private val users: MutableList<UsersRepository.User> = mutableListOf()

    override suspend fun createUser(
        userEmailAddress: EmailsRepository.EmailAddress,
        userName: UserName,
        shortBio: ShortBio?,
        creationTime: UnixTime
    ): UsersRepository.UserId {
        users += UsersRepository.User(UsersRepository.UserId(users.size), userName, userEmailAddress, shortBio, null)
        return UsersRepository.UserId(users.lastIndex)
    }

    override suspend fun getUser(userId: UsersRepository.UserId): UsersRepository.User? {
        return users.firstOrNull { it.userId == userId }
    }

    override suspend fun getUser(emailAddress: EmailsRepository.EmailAddress): UsersRepository.User? {
        return users.firstOrNull { it.emailAddress == emailAddress }
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