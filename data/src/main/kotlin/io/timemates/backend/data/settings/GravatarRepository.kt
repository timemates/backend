package io.timemates.backend.data.settings

import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.avatar.repositories.GravatarRepository as GravatarRepositoryContract

class GravatarRepository : GravatarRepositoryContract {
    override suspend fun setGravatar(email: EmailAddress) {
        TODO("Not yet implemented")
    }
}