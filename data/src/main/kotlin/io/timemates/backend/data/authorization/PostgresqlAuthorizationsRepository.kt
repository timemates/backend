package io.timemates.backend.data.authorization

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.AuthorizationId
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.data.authorization.cache.CacheAuthorizationsDataSource
import io.timemates.backend.data.authorization.cache.entities.CacheAuthorization
import io.timemates.backend.data.authorization.db.TableAuthorizationsDataSource
import io.timemates.backend.data.authorization.db.entities.DbAuthorization
import io.timemates.backend.data.authorization.mapper.AuthorizationsMapper
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.users.types.value.UserId

class PostgresqlAuthorizationsRepository(
    private val tableAuthorizationsDataSource: TableAuthorizationsDataSource,
    private val cacheAuthorizations: CacheAuthorizationsDataSource,
    private val mapper: AuthorizationsMapper,
) : AuthorizationsRepository {
    override suspend fun create(
        userId: UserId,
        accessToken: AccessHash,
        refreshToken: RefreshHash,
        expiresAt: UnixTime,
        creationTime: UnixTime,
        clientMetadata: ClientMetadata,
    ): AuthorizationId {
        val id = tableAuthorizationsDataSource.createAuthorizations(
            userId = userId.long,
            accessHash = accessToken.string,
            refreshAccessHash = refreshToken.string,
            permissions = DbAuthorization.Permissions.All,
            expiresAt = expiresAt.inMilliseconds,
            createdAt = creationTime.inMilliseconds,
            metaClientName = clientMetadata.clientName.string,
            metaClientVersion = clientMetadata.clientVersion.string,
            metaClientIpAddress = clientMetadata.clientIpAddress.string,
        )

        cacheAuthorizations.saveAuthorization(
            accessToken.string,
            CacheAuthorization(
                userId = userId.long,
                accessHash = accessToken.string,
                refreshAccessHash = refreshToken.string,
                permissions = CacheAuthorization.Permissions.All,
                expiresAt = expiresAt.inMilliseconds,
                createdAt = creationTime.inMilliseconds,
                clientMetadata = ClientMetadata(
                    clientName = clientMetadata.clientName,
                    clientVersion = clientMetadata.clientVersion,
                    clientIpAddress = clientMetadata.clientIpAddress,
                )
            )
        )

        return AuthorizationId.createOrThrow(id)
    }

    override suspend fun remove(accessToken: AccessHash): Boolean {
        cacheAuthorizations.remove(accessToken.string)
        return tableAuthorizationsDataSource.removeAuthorization(accessToken.string)
    }

    override suspend fun get(accessToken: AccessHash, afterTime: UnixTime): Authorization? {
        cacheAuthorizations.getAuthorization(accessToken.string)
            ?.let { return mapper.cacheAuthToDomainAuth(it) }

        return tableAuthorizationsDataSource.getAuthorization(accessToken.string, afterTime.inMilliseconds)
            ?.also { cacheAuthorizations.saveAuthorization(accessToken.string, mapper.dbAuthToCacheAuth(it)) }
            ?.let(mapper::dbAuthToDomainAuth)
    }

    override suspend fun getList(userId: UserId, nextPageToken: PageToken?): Page<Authorization> {
        return tableAuthorizationsDataSource.getAuthorizations(userId.long, nextPageToken)
            .map(mapper::dbAuthToDomainAuth)
    }

    override suspend fun renew(refreshToken: RefreshHash, newAccessHash: AccessHash, expiresAt: UnixTime): Authorization? {
        tableAuthorizationsDataSource.renewAccessHash(
            refreshHash = refreshToken.string,
            newAccessHash = newAccessHash.string,
            expiresAt = expiresAt.inMilliseconds,
        )

        return get(newAccessHash, UnixTime.ZERO)
    }
}