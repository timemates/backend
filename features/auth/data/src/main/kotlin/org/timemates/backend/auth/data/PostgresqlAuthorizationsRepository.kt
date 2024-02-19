package org.timemates.backend.auth.data

import com.timemates.backend.time.UnixTime
import org.timemates.backend.auth.data.cache.CacheAuthorizationsDataSource
import org.timemates.backend.auth.data.cache.entities.CacheAuthorization
import org.timemates.backend.auth.data.db.TableAuthorizationsDataSource
import org.timemates.backend.auth.data.db.entities.DbAuthorization
import org.timemates.backend.auth.data.mapper.AuthorizationsMapper
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.pagination.map
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.AuthorizationId
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
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
        val id = tableAuthorizationsDataSource.createAuthorization(
            userId = userId.long,
            accessHash = accessToken.string,
            refreshAccessHash = refreshToken.string,
            permissions = DbAuthorization.Permissions.All,
            expiresAt = expiresAt.inMilliseconds,
            createdAt = creationTime.inMilliseconds,
            metaClientName = clientMetadata.clientName.string,
            metaClientVersion = clientMetadata.clientVersion.double,
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

        return AuthorizationId.createUnsafe(id)
    }

    override suspend fun remove(accessToken: AccessHash): Boolean {
        cacheAuthorizations.remove(accessToken.string)
        return tableAuthorizationsDataSource.removeAuthorization(accessToken.string)
    }

    override suspend fun get(accessToken: AccessHash, currentTime: UnixTime): Authorization? {
        cacheAuthorizations.getAuthorization(accessToken.string)
            ?.let { return mapper.cacheAuthToDomainAuth(it) }

        return tableAuthorizationsDataSource.getAuthorization(accessToken.string, currentTime.inMilliseconds)
            ?.also { cacheAuthorizations.saveAuthorization(accessToken.string, mapper.dbAuthToCacheAuth(it)) }
            ?.let(mapper::dbAuthToDomainAuth)
    }

    override suspend fun getList(userId: UserId, nextPageToken: PageToken?): Page<Authorization> {
        return tableAuthorizationsDataSource.getAuthorizations(userId.long, nextPageToken)
            .map(mapper::dbAuthToDomainAuth)
    }

    override suspend fun renew(
        refreshToken: RefreshHash,
        newAccessHash: AccessHash,
        newRefreshHash: RefreshHash,
        expiresAt: UnixTime,
    ): Authorization? {
        tableAuthorizationsDataSource.renewAccessHash(
            refreshHash = refreshToken.string,
            newAccessHash = newAccessHash.string,
            newRefreshHash = newRefreshHash.string,
            expiresAt = expiresAt.inMilliseconds,
        )

        return get(newAccessHash, UnixTime.ZERO)?.copy(refreshAccessHash = newRefreshHash)
    }
}