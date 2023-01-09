package io.timemates.backend.repositories

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.types.value.ShortBio
import io.timemates.backend.types.value.UserName

interface UsersRepository {
    suspend fun createUser(
        userEmailAddress: EmailsRepository.EmailAddress,
        userName: UserName,
        shortBio: ShortBio? = null,
        creationTime: UnixTime
    ): UserId
    suspend fun getUser(userId: UserId): User?
    suspend fun getUsers(collection: Collection<UserId>): Collection<User>
    suspend fun edit(userId: UserId, patch: User.Patch)

    suspend fun getUser(emailAddress: EmailsRepository.EmailAddress): User?

    data class User(
        val userId: UserId,
        val name: UserName,
        val emailAddress: EmailsRepository.EmailAddress,
        val shortBio: ShortBio?,
        val avatarFileId: FilesRepository.FileId?
    ) {
        class Patch(
            val name: UserName? = null,
            val shortBio: ShortBio? = null,
            val avatarFileId: FilesRepository.FileId? = null
        )
    }

    @JvmInline
    value class UserId(val int: Int)
}