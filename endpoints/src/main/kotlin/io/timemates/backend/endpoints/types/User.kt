package io.timemates.backend.endpoints.types

import io.timemates.backend.endpoints.types.value.*
import kotlinx.serialization.Serializable
import io.timemates.backend.repositories.UsersRepository

@Serializable
class User(
    val userId: UserId,
    val name: Name,
    val shortBio: ShortBio?,
    val avatarFileId: FileId?
) {
    @Serializable
    class Patch(
        val name: Name? = null,
        val shortBio: ShortBio? = null,
        val avatarFileId: FileId? = null
    )
}

fun UsersRepository.User.serializable(): User = User(
    userId.serializable(), name.serializable(), shortBio?.serializable(), avatarFileId?.serializable()
)

fun User.Patch.internal(): UsersRepository.User.Patch = UsersRepository.User.Patch(
    name?.internalAsUserName(),
    shortBio?.internal(),
    avatarFileId?.internal()
)