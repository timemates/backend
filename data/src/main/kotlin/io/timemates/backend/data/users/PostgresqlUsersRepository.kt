package io.timemates.backend.data.users

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrowInternally
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName
import timemates.backend.hashing.repository.HashingRepository
import io.timemates.backend.users.repositories.UsersRepository as UsersRepositoryContract

class PostgresqlUsersRepository(
    private val postgresqlUsers: PostgresqlUsersDataSource,
    private val cachedUsers: CachedUsersDataSource,
    private val hashingRepository: HashingRepository,
    private val mapper: UserEntitiesMapper,
) : UsersRepositoryContract {
    override suspend fun createUser(
        userEmailAddress: EmailAddress,
        userName: UserName,
        shortBio: UserDescription?,
        creationTime: UnixTime,
    ): UserId {
        return postgresqlUsers.createUser(
            userEmailAddress.string,
            userName.string,
            shortBio?.string,
            creationTime.inMilliseconds
        ).let { UserId.createOrThrowInternally(it) }
    }

    override suspend fun isUserExists(userId: UserId): Boolean {
        if (cachedUsers.getUser(userId.long) != null)
            return true

        return postgresqlUsers.isUserExists(userId.long)
    }

    override suspend fun getUserIdByEmail(emailAddress: EmailAddress): UserId? {
        return postgresqlUsers.getUserByEmail(emailAddress.string)?.id
            ?.let { UserId.createOrThrowInternally(it) }
    }

    override suspend fun getUser(id: UserId): User? {
        return cachedUsers.getUser(id.long)?.let { mapper.toDomainUser(id.long, it) }
            ?: postgresqlUsers.getUser(id.long)?.let(mapper::toDomainUser)
    }

    override suspend fun getUser(emailAddress: EmailAddress): User? {
        return postgresqlUsers.getUserByEmail(emailAddress.string)?.let(mapper::toDomainUser)
    }

    override suspend fun getUsers(userIds: List<UserId>): List<User> {
        val fromCache = cachedUsers.getUsers(
            userIds.map(UserId::long)
        )

        val realtime = postgresqlUsers.getUsers(
            fromCache.filter { it.value == null }.map { it.key }
        )

        realtime.forEach {
            cachedUsers.putUser(
                it.key,
                mapper.toCachedUser(it.value)
            )
        }

        return userIds.map { userId ->
            fromCache[userId.long]?.let { user ->
                mapper.toDomainUser(userId.long, user)
            } ?: realtime[userId.long]?.let {
                mapper.toDomainUser(it)
            } ?: error("No User found!")
        }
    }

    override suspend fun edit(userId: UserId, patch: User.Patch): Boolean {
        postgresqlUsers.edit(userId.long, mapper.toPostgresqlUserPatch(patch))
        cachedUsers.invalidateUser(userId.long)
        return true
    }
}