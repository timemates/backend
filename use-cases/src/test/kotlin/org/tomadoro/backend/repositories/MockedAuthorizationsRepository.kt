package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.value.DateTime

class MockedAuthorizationsRepository : AuthorizationsRepository {
    private val authorizations: MutableList<AuthorizationsRepository.Authorization> =
        mutableListOf()

    override suspend fun create(
        userId: UsersRepository.UserId,
        accessToken: AuthorizationsRepository.AccessToken,
        refreshToken: AuthorizationsRepository.RefreshToken,
        expiresAt: DateTime
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
        currentTime: DateTime
    ): AuthorizationsRepository.Authorization? {
        return authorizations.firstOrNull { it.accessToken == accessToken }
    }

    override suspend fun getList(userId: UsersRepository.UserId): List<AuthorizationsRepository.Authorization> {
        return authorizations.filter { it.userId == userId }
    }

    override suspend fun renew(
        refreshToken: AuthorizationsRepository.RefreshToken,
        accessToken: AuthorizationsRepository.AccessToken,
        expiresAt: DateTime
    ): AuthorizationsRepository.Authorization {
        val index = authorizations.indexOfFirst { it.refreshToken == refreshToken }
        authorizations[index] = authorizations[index].copy(accessToken = accessToken)
        return authorizations[index]
    }

}