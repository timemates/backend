package org.tomadoro.backend.repositories

interface LinkedSocialsRepository {
    suspend fun getSocials(userId: UsersRepository.UserId): List<Social>
    suspend fun linkSocial(userId: UsersRepository.UserId, social: Social)
    suspend fun getBySocial(social: Social): UsersRepository.UserId?

    sealed interface Social {
        class Google(val accountId: Long, val email: String) : Social
    }
}