package io.timemates.backend.data.users

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName

class UserEntitiesMapper {
    fun toDomainUser(id: Long, cachedUser: CachedUsersDataSource.User): User = with(cachedUser) {
        return User(
            UserId.createOrThrow(id),
            UserName.createOrThrow(name),
            EmailAddress.createOrThrow(email),
            shortBio?.let { UserDescription.createOrThrow(it) },
            avatarFileId?.let { FileId.createOrThrow(it) }
        )
    }

    fun toCachedUser(pUser: PostgresqlUsersDataSource.User): CachedUsersDataSource.User = with(pUser) {
        return CachedUsersDataSource.User(userName, userShortDesc, userAvatarFileId, userEmail)
    }

    fun toPostgresqlUserPatch(patch: User.Patch) = with(patch) {
        PostgresqlUsersDataSource.User.Patch(name?.string, shortBio?.string, avatarId?.string)
    }

    fun toCachedUser(user: User) = with(user) {
        CachedUsersDataSource.User(
            name.string, description?.string, avatarId?.string, emailAddress.string
        )
    }

    fun toDomainUser(pUser: PostgresqlUsersDataSource.User): User = with(pUser) {
        return User(
            UserId.createOrThrow(id),
            UserName.createOrThrow(userName),
            EmailAddress.createOrThrow(userEmail),
            userShortDesc?.let { UserDescription.createOrThrow(it) },
            userAvatarFileId?.let { FileId.createOrThrow(it) }
        )
    }
}