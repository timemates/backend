package io.timemates.backend.services.users

import io.timemates.api.users.types.UserOuterClass
import io.timemates.api.users.types.user
import io.timemates.backend.users.types.User as DomainUser

class UserEntitiesMapper {
    fun toGrpcUser(domain: DomainUser): UserOuterClass.User {
        return user {
            id = domain.id.long
            name = domain.name.string
            domain.emailAddress?.string?.let { email = it }
            domain.description?.string?.let { description = it }
            domain.avatarId?.string?.let { avatarId = it }
        }
    }
}