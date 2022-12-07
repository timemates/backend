package org.tomadoro.backend.repositories

class MockedLinkedSocialsRepository : LinkedSocialsRepository {
    private val socials =
        mutableMapOf<UsersRepository.UserId, List<LinkedSocialsRepository.Social>>()

    override suspend fun getSocials(
        userId: UsersRepository.UserId
    ): List<LinkedSocialsRepository.Social> {
        return socials[userId] ?: emptyList()
    }

    override suspend fun linkSocial(
        userId: UsersRepository.UserId,
        social: LinkedSocialsRepository.Social
    ) {
        socials[userId] = ((socials[userId] ?: emptyList()).toMutableList().apply {
            add(social)
        })
    }

    override suspend fun getBySocial(social: LinkedSocialsRepository.Social): UsersRepository.UserId? {
        return socials.toList().firstOrNull { social in it.second }?.first
    }
}