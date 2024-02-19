package org.timemates.backend.users.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.types.users.User
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName

interface UsersRepository {
    suspend fun createUser(
        userEmailAddress: EmailAddress,
        userName: UserName,
        shortBio: UserDescription? = null,
        creationTime: UnixTime,
    ): UserId

    suspend fun isUserExists(userId: UserId): Boolean

    suspend fun getUserIdByEmail(emailAddress: EmailAddress): UserId?

    suspend fun getUser(id: UserId): User?

    /**
     * Gets users by [userIds].
     *
     * @return [List] of users where order to [userIds] is guaranteed.
     */
    suspend fun getUsers(userIds: List<UserId>): List<User>
    suspend fun edit(userId: UserId, patch: User.Patch): Boolean

    suspend fun getUser(emailAddress: EmailAddress): User?
}