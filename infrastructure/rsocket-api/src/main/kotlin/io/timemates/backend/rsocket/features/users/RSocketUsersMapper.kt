package io.timemates.backend.rsocket.features.users

import io.timemates.api.rsocket.serializable.types.users.SerializableAvatar
import io.timemates.api.rsocket.serializable.types.users.SerializableUserPatch
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketMapper
import io.timemates.backend.users.types.value.UserDescription
import io.timemates.backend.users.types.value.UserName
import io.timemates.backend.users.types.Avatar as DomainAvatar
import io.timemates.backend.users.types.User as DomainUser

class RSocketUsersMapper : RSocketMapper {

    fun toDomainUserPatch(userPatch: SerializableUserPatch): DomainUser.Patch = with(userPatch) {
        return@with DomainUser.Patch(
            name = name?.let { UserName.createOrFail(it) },
            description = description?.let { UserDescription.createOrFail(it) },
            avatar = avatar?.let(::toDomainUserAvatar),
        )
    }

    private fun toDomainUserAvatar(avatar: SerializableAvatar): DomainAvatar {
        return when (avatar) {
            is SerializableAvatar.TimeMates -> DomainAvatar.FileId.createOrFail(avatar.fileId)
            is SerializableAvatar.Gravatar -> DomainAvatar.GravatarId.createOrFail(avatar.gravatarId)
        }
    }
}