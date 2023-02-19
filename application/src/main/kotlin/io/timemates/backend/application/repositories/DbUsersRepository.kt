package io.timemates.backend.application.repositories

import io.timemates.backend.integrations.cache.storage.UsersCacheDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.DbUsersDatabaseDataSource
import io.timemates.backend.repositories.EmailsRepository
import io.timemates.backend.repositories.FilesRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.UserName

class DbUsersRepository(
    private val databaseDataSource: DbUsersDatabaseDataSource,
    private val usersCacheDataSource: UsersCacheDataSource
) : UsersRepository {
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
        return usersCacheDataSource.getUser(userId.int)?.toExternalUser(userId.int)
            ?: databaseDataSource.getUser(userId.int)?.toExternalUser()
    }

    override suspend fun getUser(emailAddress: EmailsRepository.EmailAddress): UsersRepository.User? {
        return databaseDataSource.getUserByEmail(emailAddress.string)?.toExternalUser()
    }

    override suspend fun getUsers(collection: Collection<UsersRepository.UserId>): Collection<UsersRepository.User> {
        val fromCache = usersCacheDataSource.getUsers(
            collection.map(UsersRepository.UserId::int)
        )

        val realtime = databaseDataSource.getUsers(
            fromCache.filter { it.value == null }.map { it.key }
        ).map { it.toExternalUser() }

        realtime.forEach {
            usersCacheDataSource.putUser(
                it.userId.int,
                it.toInternal()
            )
        }

        return fromCache.mapNotNull { it.value?.toExternalUser(it.key) } + realtime
    }

    override suspend fun edit(userId: UsersRepository.UserId, patch: UsersRepository.User.Patch) {
        databaseDataSource.edit(userId.int, patch.toInternal())
        usersCacheDataSource.invalidateUser(userId.int)
    }

    private fun UsersRepository.User.Patch.toInternal() =
        DbUsersDatabaseDataSource.User.Patch(name?.string, shortBio?.string, avatarFileId?.string)

    private fun UsersRepository.User.toInternal() =
        UsersCacheDataSource.User(
            name.string, shortBio?.string, avatarFileId?.string, emailAddress.string
        )

    private fun DbUsersDatabaseDataSource.User.toExternalUser(): UsersRepository.User {
        return UsersRepository.User(
            UsersRepository.UserId(id),
            UserName(userName),
            EmailsRepository.EmailAddress(userEmail),
            userShortDesc?.let { ShortBio(it) },
            userAvatarFileId?.let { FilesRepository.FileId(it) }
        )
    }

    private fun UsersCacheDataSource.User.toExternalUser(id: Int): UsersRepository.User {
        return UsersRepository.User(
            UsersRepository.UserId(id),
            UserName(name),
            EmailsRepository.EmailAddress(email),
            shortBio?.let { ShortBio(it) },
            avatarFileId?.let { FilesRepository.FileId(it) }
        )
    }
}