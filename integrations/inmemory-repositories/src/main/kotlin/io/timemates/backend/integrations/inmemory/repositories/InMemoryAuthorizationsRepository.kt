package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.value.UnixTime

class InMemoryAuthorizationsRepository : AuthorizationsRepository {
    private val authorizations: MutableList<AuthorizationsRepository.Authorization> =
        mutableListOf()

    override suspend fun create(
        userId: UsersRepository.UserId,
        accessToken: AuthorizationsRepository.AccessToken,
        refreshToken: AuthorizationsRepository.RefreshToken,
        expiresAt: UnixTime
    ) {
        authorizations += AuthorizationsRepository.Authorization(
            userId,
            accessToken,
            refreshToken,
            expiresAt
        )
    }

    override suspend fun remove(accessToken: AuthorizationsRepository.AccessToken): Boolean {
        return authorizations.removeIf { it.accessToken == accessToken }
    }

    override suspend fun get(
        accessToken: AuthorizationsRepository.AccessToken,
        currentTime: UnixTime
    ): AuthorizationsRepository.Authorization? {
        return authorizations.firstOrNull { it.accessToken == accessToken }
    }

    override suspend fun getList(userId: UsersRepository.UserId): List<AuthorizationsRepository.Authorization> {
        return authorizations.filter { it.userId == userId }
    }

    override suspend fun renew(
        refreshToken: AuthorizationsRepository.RefreshToken,
        accessToken: AuthorizationsRepository.AccessToken,
        expiresAt: UnixTime
    ): AuthorizationsRepository.Authorization? {
        val index = authorizations
            .indexOfFirst { it.refreshToken == refreshToken }
            .takeIf { it >= 0 } ?: return null
        authorizations[index] = authorizations[index].copy(accessToken = accessToken)
        return authorizations[index]
    }

}