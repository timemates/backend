package io.timemates.backend.avatar.repositories

import io.timemates.backend.users.types.value.EmailAddress

interface GravatarRepository {

    suspend fun setGravatar(email: EmailAddress)
}