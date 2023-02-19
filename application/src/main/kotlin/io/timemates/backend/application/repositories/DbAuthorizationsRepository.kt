package io.timemates.backend.application.repositories

import io.timemates.backend.types.value.UnixTime
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.AuthorizationsDataSource
import io.timemates.backend.integrations.postgresql.repositories.datasource.tables.AuthorizationsTable
import io.timemates.backend.repositories.AuthorizationsRepository as Contract

class DbAuthorizationsRepository(
    private val datasource: AuthorizationsDataSource
) : Contract {

    override suspend fun create(
        userId: UsersRepository.UserId,
        accessToken: AuthorizationsRepository.AccessToken,
        refreshToken: AuthorizationsRepository.RefreshToken,
        expiresAt: UnixTime
    ) {
        datasource.create(userId, accessToken, refreshToken, expiresAt)
    }

    override suspend fun remove(accessToken: AuthorizationsRepository.AccessToken): Boolean {
        return datasource.remove(accessToken)
    }

    override suspend fun get(
        accessToken: AuthorizationsRepository.AccessToken,
        currentTime: UnixTime
    ): AuthorizationsRepository.Authorization? {
        return datasource.get(accessToken, currentTime)?.toExternal()
    }

    override suspend fun getList(userId: UsersRepository.UserId): List<AuthorizationsRepository.Authorization> {
        return datasource.getList(userId).map { it.toExternal() }
    }

    override suspend fun renew(
        refreshToken: AuthorizationsRepository.RefreshToken,
        accessToken: AuthorizationsRepository.AccessToken,
        expiresAt: UnixTime
    ): AuthorizationsRepository.Authorization? {
        return datasource.renew(refreshToken, accessToken, expiresAt)?.toExternal()
    }

    private fun AuthorizationsTable.Authorization.toExternal(): AuthorizationsRepository.Authorization {
        return AuthorizationsRepository.Authorization(
            userId, accessToken, refreshToken, expiresAt
        )
    }
}