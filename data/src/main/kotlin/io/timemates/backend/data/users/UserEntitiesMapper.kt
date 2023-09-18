package io.timemates.backend.data.users

import io.timemates.backend.validation.createOrThrowInternally
import io.timemates.backend.data.common.markers.Mapper
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.users.types.Avatar
import io.timemates.backend.users.types.User
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName

class UserEntitiesMapper : Mapper {
    fun toDomainUser(id: Long, cachedUser: CachedUsersDataSource.User): User = with(cachedUser) {
        return User(
            UserId.createOrThrowInternally(id),
            UserName.createOrThrowInternally(name),
            email?.let { EmailAddress.createOrThrowInternally(it) },
            shortBio?.let { UserDescription.createOrThrowInternally(it) },
            avatar = avatarFileId?.let { Avatar.FileId.createOrThrowInternally(it) }
                ?: gravatarId?.let { Avatar.GravatarId.createOrThrowInternally(it) }
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
            UserId.createOrThrowInternally(id),
            UserName.createOrThrowInternally(userName),
            EmailAddress.createOrThrowInternally(userEmail),
            userShortDesc?.let { UserDescription.createOrThrowInternally(it) },
            avatar = pUser.userAvatarFileId?.let { Avatar.FileId.createOrThrowInternally(it) }
                ?: pUser.userGravatarId?.let { Avatar.GravatarId.createOrThrowInternally(it) }
        )
    }
}