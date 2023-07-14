package io.timemates.backend.users.types

import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.users.types.value.*

data class User(
    val id: UserId,
    val name: UserName,
    val emailAddress: EmailAddress?,
    val description: UserDescription?,
    val avatar: Avatar?,
) {
    data class Patch(
        val name: UserName? = null,
        val shortBio: UserDescription? = null,
        val avatarId: FileId? = null,
    )
}