package org.timemates.backend.auth.adapters

import com.timemates.backend.time.UnixTime
import org.timemates.backend.auth.domain.repositories.UsersRepository
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.types.users.value.UserDescription
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName
import org.timemates.backend.users.domain.repositories.UsersRepository as UsersRepositoryDelegate

class UsersRepositoryDelegateAdapter(private val delegate: UsersRepositoryDelegate) : UsersRepository {
    override suspend fun create(
        emailAddress: EmailAddress,
        userName: UserName,
        userDescription: UserDescription?,
        creationTime: UnixTime,
    ): Result<UserId> = runCatching {
        delegate.createUser(emailAddress, userName, userDescription, creationTime)
    }

    override suspend fun get(emailAddress: EmailAddress): Result<UserId?> = runCatching {
        delegate.getUserIdByEmail(emailAddress)
    }

}