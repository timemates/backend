package io.timemates.backend.data.users

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.users.types.Avatar
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.*

class UserEntitiesMapper {
    fun toDomainUser(id: Long, cachedUser: CachedUsersDataSource.User): User = with(cachedUser) {
        return User(
            UserId.createOrThrow(id),
            UserName.createOrThrow(name),
            email?.let { EmailAddress.createOrThrow(it) },
            shortBio?.let { UserDescription.createOrThrow(it) },
            avatar = avatarFileId?.let { Avatar.FileId.createOrThrow(it) } ?:
                gravatarId?.let { Avatar.GravatarId.createOrThrow(it) }
        )
    }

    fun toCachedUser(pUser: PostgresqlUsersDataSource.User): CachedUsersDataSource.User = with(pUser) {
        return CachedUsersDataSource.User(userName, userShortDesc, userAvatarFileId, userGravatarId, userEmail)
    }

    fun toPostgresqlUserPatch(patch: User.Patch) = with(patch) {
        PostgresqlUsersDataSource.User.Patch(name?.string, shortBio?.string, avatarId?.string, gravatarId?.string)
    }

    fun toCachedUser(user: User) = with(user) {
        CachedUsersDataSource.User(
            name.string,
            description?.string,
            avatarFileId = (avatar as? Avatar.FileId)?.string,
            gravatarId = (avatar as? Avatar.GravatarId)?.string,
            emailAddress?.string
        )
    }

    fun toDomainUser(pUser: PostgresqlUsersDataSource.User): User = with(pUser) {
        return User(
            UserId.createOrThrow(id),
            UserName.createOrThrow(userName),
            EmailAddress.createOrThrow(userEmail),
            userShortDesc?.let { UserDescription.createOrThrow(it) },
            avatar = pUser.userAvatarFileId?.let { Avatar.FileId.createOrThrow(it) } ?:
            pUser.userGravatarId?.let { Avatar.GravatarId.createOrThrow(it) }
        )
    }
}