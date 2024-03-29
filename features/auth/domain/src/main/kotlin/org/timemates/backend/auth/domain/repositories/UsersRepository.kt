package org.timemates.backend.auth.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName

interface UsersRepository {
    suspend fun create(
        emailAddress: EmailAddress,
        userName: UserName,
        userDescription: UserDescription?,
        creationTime: UnixTime,
    ): Result<UserId>

    suspend fun get(emailAddress: EmailAddress): Result<UserId?>
}