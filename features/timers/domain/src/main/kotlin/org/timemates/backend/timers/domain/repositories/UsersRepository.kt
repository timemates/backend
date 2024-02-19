package org.timemates.backend.timers.domain.repositories

import org.timemates.backend.types.users.User
import org.timemates.backend.types.users.value.UserId

interface UsersRepository {

    /**
     * Gets users by [userIds].
     *
     * @return [List] of users where order to [userIds] is guaranteed.
     */
    suspend fun getUsers(userIds: List<UserId>): List<User>
}