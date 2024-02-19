package org.timemates.backend.types.users

import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName

data class User(
    val id: UserId,
    val name: UserName,
    val emailAddress: org.timemates.backend.types.users.value.EmailAddress?,
    val description: UserDescription?,
    val avatar: Avatar?,
) {
    data class Patch(
        val name: UserName? = null,
        val description: UserDescription? = null,
        val avatar: Avatar?,
    )
}