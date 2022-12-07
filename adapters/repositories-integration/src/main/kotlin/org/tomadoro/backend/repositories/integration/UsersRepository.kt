package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.UserName
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.datasource.UsersDatabaseDataSource
import org.tomadoro.backend.repositories.UsersRepository as UsersRepositoryContract

class UsersRepository(
    private val databaseDataSource: UsersDatabaseDataSource
) : UsersRepositoryContract {

    override suspend fun createUser(userName: UserName, creationTime: DateTime): UsersRepository.UserId {
        return databaseDataSource.createUser(userName.string, creationTime.long)
            .let { UsersRepository.UserId(it) }
    }

    override suspend fun getUser(userId: UsersRepository.UserId): UsersRepository.User? {
        return databaseDataSource.getUser(userId.int)?.toExternalUser()
    }

    private fun UsersDatabaseDataSource.User.toExternalUser(): UsersRepositoryContract.User {
        return UsersRepositoryContract.User(UsersRepository.UserId(id), UserName(userName))
    }
}