package org.timemates.backend.auth.data.mapper

import com.timemates.backend.time.UnixTime
import org.timemates.backend.auth.data.cache.entities.CacheAuthorization
import org.timemates.backend.auth.data.db.entities.DbAuthorization
import org.timemates.backend.foundation.authorization.Scope
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.AuthorizationsScope
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.metadata.value.ClientIpAddress
import org.timemates.backend.types.auth.metadata.value.ClientName
import org.timemates.backend.types.auth.metadata.value.ClientVersion
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.timers.TimersScope
import org.timemates.backend.types.users.UsersScope
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.validation.annotations.ValidationDelicateApi
import org.timemates.backend.validation.createUnsafe
import org.timemates.backend.auth.data.cache.entities.CacheAuthorization.Permissions.GrantLevel as CacheGrantLevel
import org.timemates.backend.auth.data.db.entities.DbAuthorization.Permissions.GrantLevel as DbGrantLevel

@OptIn(ValidationDelicateApi::class)
class AuthorizationsMapper {
    fun dbAuthToDomainAuth(auth: DbAuthorization): Authorization = with(auth) {
        return Authorization(
            userId = UserId.createUnsafe(auth.userId),
            accessHash = AccessHash.createUnsafe(auth.accessHash),
            refreshAccessHash = null, // intended: we don't return refresh hash to public
            scopes = dbPermissionsToDomain(permissions),
            expiresAt = UnixTime.createUnsafe(expiresAt),
            createdAt = UnixTime.createUnsafe(createdAt),
            clientMetadata = ClientMetadata(
                clientName = ClientName.createUnsafe(auth.metaClientName),
                clientVersion = ClientVersion.createUnsafe(auth.metaClientVersion),
                clientIpAddress = ClientIpAddress.createUnsafe(auth.metaClientIpAddress),
            )
        )
    }

    fun dbAuthToCacheAuth(auth: DbAuthorization): CacheAuthorization = with(auth) {
        return CacheAuthorization(
            userId = userId,
            accessHash = accessHash,
            refreshAccessHash = refreshAccessHash,
            permissions = dbPermissionsToCachePermissions(permissions),
            expiresAt = expiresAt,
            createdAt = createdAt,
            clientMetadata = ClientMetadata(
                clientName = ClientName.createUnsafe(auth.metaClientName),
                clientVersion = ClientVersion.createUnsafe(auth.metaClientVersion),
                clientIpAddress = ClientIpAddress.createUnsafe(auth.metaClientIpAddress),
            ),
        )
    }

    private fun dbPermissionsToCachePermissions(
        auth: DbAuthorization.Permissions,
    ): CacheAuthorization.Permissions = with(auth) {
        return CacheAuthorization.Permissions(
            dbGrantLevelToCacheGrantLevel(authorization),
            dbGrantLevelToCacheGrantLevel(users),
            dbGrantLevelToCacheGrantLevel(timers),
        )
    }

    private fun dbGrantLevelToCacheGrantLevel(
        level: DbGrantLevel,
    ): CacheAuthorization.Permissions.GrantLevel {
        return when (level) {
            DbGrantLevel.READ -> CacheAuthorization.Permissions.GrantLevel.READ
            DbGrantLevel.WRITE -> CacheAuthorization.Permissions.GrantLevel.WRITE
            DbGrantLevel.NOT_GRANTED -> CacheAuthorization.Permissions.GrantLevel.NOT_GRANTED
        }
    }

    fun cacheAuthToDomainAuth(auth: CacheAuthorization): Authorization = with(auth) {
        return Authorization(
            userId = UserId.createUnsafe(auth.userId),
            accessHash = AccessHash.createUnsafe(auth.accessHash),
            refreshAccessHash = RefreshHash.createUnsafe(auth.refreshAccessHash),
            scopes = cachePermissionsToDomain(permissions),
            expiresAt = UnixTime.createUnsafe(expiresAt),
            createdAt = UnixTime.createUnsafe(createdAt),
            clientMetadata = ClientMetadata(
                clientName = ClientName.createUnsafe(auth.clientMetadata.clientName.string),
                clientVersion = ClientVersion.createUnsafe(auth.clientMetadata.clientVersion.double),
                clientIpAddress = ClientIpAddress.createUnsafe(auth.clientMetadata.clientIpAddress.string),
            )
        )
    }

    private fun dbPermissionsToDomain(
        dbPermissions: DbAuthorization.Permissions,
    ): List<Scope> = with(dbPermissions) {
        return buildList {
            if (authorization != DbGrantLevel.NOT_GRANTED) {
                add(
                    if (authorization == DbGrantLevel.WRITE)
                        AuthorizationsScope.Write
                    else AuthorizationsScope.Read
                )
            }

            if (users != DbGrantLevel.NOT_GRANTED) {
                add(
                    if (users == DbGrantLevel.WRITE)
                        UsersScope.Write
                    else UsersScope.Read
                )
            }

            if (timers != DbGrantLevel.NOT_GRANTED) {
                add(
                    if (timers == DbGrantLevel.WRITE)
                        TimersScope.Write
                    else TimersScope.Read
                )
            }
        }
    }

    private fun cachePermissionsToDomain(
        cache: CacheAuthorization.Permissions,
    ): List<Scope> = with(cache) {
        return buildList {
            if (authorization != CacheGrantLevel.NOT_GRANTED) {
                add(
                    if (authorization == CacheGrantLevel.WRITE)
                        AuthorizationsScope.Write
                    else AuthorizationsScope.Read
                )
            }

            if (users != CacheGrantLevel.NOT_GRANTED) {
                add(
                    if (users == CacheGrantLevel.WRITE)
                        UsersScope.Write
                    else UsersScope.Read
                )
            }

            if (timers != CacheGrantLevel.NOT_GRANTED) {
                add(
                    if (timers == CacheGrantLevel.WRITE)
                        TimersScope.Write
                    else TimersScope.Read
                )
            }
        }
    }
}