package io.timemates.backend.auth.data.cache.mapper

import io.timemates.backend.auth.data.cache.entities.CacheAuthorization
import io.timemates.backend.auth.data.db.entities.DbAuthorization
import io.timemates.backend.types.auth.metadata.ClientMetadata
import io.timemates.backend.types.auth.metadata.value.ClientIpAddress
import io.timemates.backend.types.auth.metadata.value.ClientName
import io.timemates.backend.types.auth.metadata.value.ClientVersion
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
class CacheAuthorizationsMapper {
    fun dbToCacheAuthorization(auth: DbAuthorization): CacheAuthorization = with(auth) {
        CacheAuthorization(
            userId = userId,
            accessHash = accessHash,
            refreshAccessHash = refreshAccessHash,
            permissions = dbPermissionsToCachePermissions(permissions),
            expiresAt = expiresAt,
            createdAt = createdAt,
            clientMetadata = ClientMetadata(
                clientName = ClientName.createUnsafe(metaClientName),
                clientVersion = ClientVersion.createUnsafe(metaClientVersion),
                clientIpAddress = ClientIpAddress.createUnsafe(metaClientIpAddress),
            )
        )
    }

    private fun dbPermissionsToCachePermissions(
        permissions: DbAuthorization.Permissions,
    ): CacheAuthorization.Permissions = with(permissions) {
        CacheAuthorization.Permissions(
            authorization = authorization.toCacheGrantLevel(),
            users = users.toCacheGrantLevel(),
            timers = timers.toCacheGrantLevel(),
        )
    }

    private fun DbAuthorization.Permissions.GrantLevel.toCacheGrantLevel(): CacheAuthorization.Permissions.GrantLevel =
        when (this) {
            DbAuthorization.Permissions.GrantLevel.READ -> CacheAuthorization.Permissions.GrantLevel.READ
            DbAuthorization.Permissions.GrantLevel.WRITE -> CacheAuthorization.Permissions.GrantLevel.WRITE
            DbAuthorization.Permissions.GrantLevel.NOT_GRANTED -> CacheAuthorization.Permissions.GrantLevel.NOT_GRANTED
        }
}