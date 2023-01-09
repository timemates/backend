package io.timemates.backend.integrations.postgresql.repositories

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UserName
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.FilesRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.DbUsersDatabaseDataSource
import io.timemates.backend.repositories.UsersRepository as DbUsersRepositoryContract

class DbUsersRepository(
    private val databaseDataSource: DbUsersDatabaseDataSource
) : DbUsersRepositoryContract {

    override suspend fun createUser(
        userEmailAddress: EmailsRepository.EmailAddress,
        userName: UserName,
        shortBio: ShortBio?,
        creationTime: UnixTime
    ): UsersRepository.UserId {
        return databaseDataSource.createUser(
            userEmailAddress.string,
            userName.string,
            shortBio?.string,
            creationTime.long
        ).let { UsersRepository.UserId(it) }
    }

    override suspend fun getUser(userId: UsersRepository.UserId): UsersRepository.User? {
        return databaseDataSource.getUser(userId.int)?.toExternalUser()
    }

    override suspend fun getUser(emailAddress: EmailsRepository.EmailAddress): UsersRepository.User? {
        return databaseDataSource.getUserByEmail(emailAddress.string)?.toExternalUser()
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

    private fun DbUsersDatabaseDataSource.User.toExternalUser(): DbUsersRepositoryContract.User {
        return DbUsersRepositoryContract.User(
            UsersRepository.UserId(id),
            UserName(userName),
            EmailsRepository.EmailAddress(userEmail),
            userShortDesc?.let { ShortBio(it) },
            userAvatarFileId?.let { FilesRepository.FileId(it) }
        )
    }
}