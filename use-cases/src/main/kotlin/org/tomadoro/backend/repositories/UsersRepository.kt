package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.ShortBio
import org.tomadoro.backend.domain.UserName

interface UsersRepository {
    suspend fun createUser(
        userName: UserName,
        shortBio: ShortBio? = null,
        creationTime: DateTime
    ): UserId
    suspend fun getUser(userId: UserId): User?
    suspend fun getUsers(collection: Collection<UserId>): Collection<User>
    suspend fun edit(userId: UserId, patch: User.Patch)

    data class User(
        val userId: UserId,
        val name: UserName,
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