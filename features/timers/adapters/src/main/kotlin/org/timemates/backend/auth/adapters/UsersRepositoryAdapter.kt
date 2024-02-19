package org.timemates.backend.auth.adapters

import org.timemates.backend.timers.domain.repositories.UsersRepository
import org.timemates.backend.types.users.User
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.users.domain.repositories.UsersRepository as UsersRepositoryDelegate

class UsersRepositoryAdapter(
    private val delegate: UsersRepositoryDelegate,
) : UsersRepository {
    override suspend fun getUsers(userIds: List<UserId>): List<User> {
        return delegate.getUsers(userIds)
    }
}