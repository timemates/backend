package io.timemates.backend.rsocket.users

import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketMapper
import io.timemates.backend.users.types.Avatar
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName
import io.timemates.backend.rsocket.users.types.User as RSocketUser
import io.timemates.backend.users.types.Avatar as DomainAvatar
import io.timemates.backend.users.types.User as DomainUser

class RSocketUsersMapper : RSocketMapper {
    fun toRSocketUser(user: DomainUser): RSocketUser = with(user) {
        return@with RSocketUser(
            id.long,
            name.string,
            emailAddress?.string,
            description = description?.string,
            avatar = avatar?.let { toRSocketUserAvatar(it) },
        )
    }

    fun toDomainUserPatch(userPatch: RSocketUser.Patch): DomainUser.Patch = with(userPatch) {
        return@with DomainUser.Patch(
            name = name?.let { UserName.createOrFail(it) },
            description = description?.let { UserDescription.createOrFail(it) },
            avatarId = avatar?.let(::toDomainUserAvatar)
        )
    }

    private fun toDomainUserAvatar(avatar: RSocketUser.Avatar): DomainAvatar {
        return when (avatar) {
            is RSocketUser.Avatar.TimeMates -> DomainAvatar.FileId.createOrFail(avatar.fileId)
            is RSocketUser.Avatar.Gravatar -> DomainAvatar.GravatarId.createOrFail(avatar.gravatarId)
        }
    }

    private fun toRSocketUserAvatar(avatar: DomainAvatar): RSocketUser.Avatar {
        return when (avatar) {
            is DomainAvatar.FileId -> RSocketUser.Avatar.TimeMates(avatar.string)
            is DomainAvatar.GravatarId -> RSocketUser.Avatar.Gravatar(avatar.string)
        }
    }
}