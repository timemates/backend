package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.domain.value.DateTime
import org.tomadoro.backend.domain.value.ShortBio
import org.tomadoro.backend.domain.value.UserName
import org.tomadoro.backend.repositories.FilesRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.datasource.DbUsersDatabaseDataSource
import org.tomadoro.backend.repositories.UsersRepository as UsersRepositoryContract

class UsersRepository(
    private val databaseDataSource: DbUsersDatabaseDataSource
) : UsersRepositoryContract {

    override suspend fun createUser(
        userName: UserName,
        shortBio: ShortBio?,
        creationTime: DateTime
    ): UsersRepository.UserId {
        return databaseDataSource.createUser(
            userName.string,
            shortBio?.string,
            creationTime.long
        ).let { UsersRepository.UserId(it) }
    }

    override suspend fun getUser(userId: UsersRepository.UserId): UsersRepository.User? {
        return databaseDataSource.getUser(userId.int)?.toExternalUser()
    }

    override suspend fun getUsers(collection: Collection<UsersRepository.UserId>): Collection<UsersRepository.User> {
        return databaseDataSource.getUsers(
            collection.map(UsersRepository.UserId::int)
        ).map { it.toExternalUser() }
    }

    override suspend fun edit(userId: UsersRepository.UserId, patch: UsersRepository.User.Patch) {
        databaseDataSource.edit(userId.int, patch.toInternal())
    }

    private fun UsersRepository.User.Patch.toInternal() =
        DbUsersDatabaseDataSource.User.Patch(name?.string, shortBio?.string, avatarFileId?.string)

    private fun DbUsersDatabaseDataSource.User.toExternalUser(): UsersRepositoryContract.User {
        return UsersRepositoryContract.User(
            UsersRepository.UserId(id),
            UserName(userName),
            userShortDesc?.let { ShortBio(it) },
            userAvatarFileId?.let { FilesRepository.FileId(it) }
        )
    }
}