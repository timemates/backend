package io.timemates.backend.types.users

import io.timemates.backend.types.users.value.EmailAddress
import io.timemates.backend.types.users.value.UserDescription
import io.timemates.backend.types.users.value.UserId
import io.timemates.backend.types.users.value.UserName

data class User(
    val id: UserId,
    val name: UserName,
    val emailAddress: EmailAddress?,
    val description: UserDescription?,
    val avatar: Avatar?,
) {
    data class Patch(
        val name: UserName? = null,
        val description: UserDescription? = null,
        val avatar: Avatar?,
    )
}