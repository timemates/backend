package org.timemates.backend.users.data

import org.timemates.backend.types.users.Avatar
import org.timemates.backend.types.users.User
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName
import org.timemates.backend.users.data.datasource.CachedUsersDataSource
import org.timemates.backend.users.data.datasource.PostgresqlUsersDataSource
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
class UserEntitiesMapper {
    fun toDomainUser(id: Long, cachedUser: CachedUsersDataSource.User): User = with(cachedUser) {
        return User(
            UserId.createUnsafe(id),
            UserName.createUnsafe(name),
            email?.let { EmailAddress.createUnsafe(it) },
            shortBio?.let { UserDescription.createUnsafe(it) },
            avatar = avatarFileId?.let { Avatar.FileId.createUnsafe(it) }
                ?: gravatarId?.let { Avatar.GravatarId.createUnsafe(it) }
        )
    }

    fun toCachedUser(pUser: PostgresqlUsersDataSource.User): CachedUsersDataSource.User = with(pUser) {
        return CachedUsersDataSource.User(userName, userShortDesc, userAvatarFileId, userGravatarId, userEmail)
    }

    fun toPostgresqlUserPatch(patch: User.Patch) = with(patch) {
        PostgresqlUsersDataSource.User.Patch(
            userName = name?.string,
            userShortDesc = description?.string,
            userAvatarFileId = (avatar as? Avatar.FileId)?.string,
            userGravatarId = (avatar as? Avatar.GravatarId)?.string
        )
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
            UserId.createUnsafe(id),
            UserName.createUnsafe(userName),
            EmailAddress.createUnsafe(userEmail),
            userShortDesc?.let { UserDescription.createUnsafe(it) },
            avatar = pUser.userAvatarFileId?.let { Avatar.FileId.createUnsafe(it) }
                ?: pUser.userGravatarId?.let { Avatar.GravatarId.createUnsafe(it) }
        )
    }
}