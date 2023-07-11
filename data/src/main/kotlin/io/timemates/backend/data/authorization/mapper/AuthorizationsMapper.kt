package io.timemates.backend.data.authorization.mapper

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.AuthorizationsScope
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.data.authorization.cache.entities.CacheAuthorization
import io.timemates.backend.data.authorization.db.entities.DbAuthorization
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.files.types.FilesScope
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.UsersScope
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.data.authorization.cache.entities.CacheAuthorization.Permissions.GrantLevel as CacheGrantLevel
import io.timemates.backend.data.authorization.db.entities.DbAuthorization.Permissions.GrantLevel as DbGrantLevel

class AuthorizationsMapper {
    fun dbAuthToDomainAuth(auth: DbAuthorization): Authorization = with(auth) {
        return Authorization(
            userId = UserId.createOrThrow(auth.userId),
            accessHash = AccessHash.createOrThrow(auth.accessHash),
            refreshAccessHash = RefreshHash.createOrThrow(auth.refreshAccessHash),
            scopes = dbPermissionsToDomain(permissions),
            expiresAt = UnixTime.createOrThrow(expiresAt),
            createdAt = UnixTime.createOrThrow(createdAt),
            clientMetadata = ClientMetadata(
                clientName = ClientName.createOrThrow(auth.metaClientName),
                clientVersion = ClientVersion.createOrThrow(auth.metaClientVersion),
                clientIpAddress = ClientIpAddress.createOrThrow(auth.metaClientVersion),
            )
        )
    }

    fun dbAuthToCacheAuth(auth: DbAuthorization): CacheAuthorization = with(auth) {
        return CacheAuthorization(
            userId,
            accessHash,
            refreshAccessHash,
            dbPermissionsToCachePermissions(permissions),
            expiresAt,
            createdAt,
            ClientMetadata(
                clientName = ClientName.createOrThrow(auth.metaClientName),
                clientVersion = ClientVersion.createOrThrow(auth.metaClientVersion),
                clientIpAddress = ClientIpAddress.createOrThrow(auth.metaClientVersion),
            )
        )
    }

    private fun dbPermissionsToCachePermissions(
        auth: DbAuthorization.Permissions,
    ): CacheAuthorization.Permissions = with(auth) {
        return CacheAuthorization.Permissions(
            dbGrantLevelToCacheGrantLevel(authorization),
            dbGrantLevelToCacheGrantLevel(users),
            dbGrantLevelToCacheGrantLevel(timers),
            dbGrantLevelToCacheGrantLevel(files),
        )
    }

    private fun dbGrantLevelToCacheGrantLevel(
        level: DbAuthorization.Permissions.GrantLevel,
    ): CacheAuthorization.Permissions.GrantLevel {
        return when (level) {
            DbAuthorization.Permissions.GrantLevel.READ -> CacheAuthorization.Permissions.GrantLevel.READ
            DbAuthorization.Permissions.GrantLevel.WRITE -> CacheAuthorization.Permissions.GrantLevel.WRITE
            DbAuthorization.Permissions.GrantLevel.NOT_GRANTED -> CacheAuthorization.Permissions.GrantLevel.NOT_GRANTED
        }
    }

    fun cacheAuthToDomainAuth(auth: CacheAuthorization): Authorization = with(auth) {
        return Authorization(
            userId = UserId.createOrThrow(auth.userId),
            accessHash = AccessHash.createOrThrow(auth.accessHash),
            refreshAccessHash = RefreshHash.createOrThrow(auth.refreshAccessHash),
            scopes = cachePermissionsToDomain(permissions),
            expiresAt = UnixTime.createOrThrow(expiresAt),
            createdAt = UnixTime.createOrThrow(createdAt),
            clientMetadata = ClientMetadata(
                clientName = ClientName.createOrThrow(auth.clientMetadata.clientName.string),
                clientVersion = ClientVersion.createOrThrow(auth.clientMetadata.clientVersion.string),
                clientIpAddress = ClientIpAddress.createOrThrow(auth.clientMetadata.clientIpAddress.string),
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

            if (files != DbGrantLevel.NOT_GRANTED) {
                add(
                    if (files == DbGrantLevel.WRITE)
                        FilesScope.Write
                    else FilesScope.Read
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

            if (files != CacheGrantLevel.NOT_GRANTED) {
                add(
                    if (files == CacheGrantLevel.WRITE)
                        FilesScope.Write
                    else FilesScope.Read
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