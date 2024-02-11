package io.timemates.backend.users.data

import com.timemates.backend.time.UnixTime
import io.timemates.backend.types.users.User
import io.timemates.backend.types.users.value.EmailAddress
import io.timemates.backend.types.users.value.UserDescription
import io.timemates.backend.types.users.value.UserId
import io.timemates.backend.types.users.value.UserName
import io.timemates.backend.users.data.datasource.CachedUsersDataSource
import io.timemates.backend.users.data.datasource.PostgresqlUsersDataSource
import io.timemates.backend.users.domain.repositories.UsersRepository
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
import timemates.backend.hashing.HashingRepository

class PostgresqlUsersRepository(
    private val postgresqlUsers: PostgresqlUsersDataSource,
    private val cachedUsers: CachedUsersDataSource,
    private val hashingRepository: HashingRepository,
    private val mapper: UserEntitiesMapper,
) : UsersRepository {
    @OptIn(ValidationDelicateApi::class)
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
        ).let { UserId.createUnsafe(it) }
    }

    override suspend fun isUserExists(userId: UserId): Boolean {
        if (cachedUsers.getUser(userId.long) != null)
            return true

        return postgresqlUsers.isUserExists(userId.long)
    }

    @OptIn(ValidationDelicateApi::class)
    override suspend fun getUserIdByEmail(emailAddress: EmailAddress): UserId? {
        return postgresqlUsers.getUserByEmail(emailAddress.string)?.id
            ?.let { UserId.createUnsafe(it) }
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