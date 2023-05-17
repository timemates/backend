package io.timemates.backend.data.authorization.cache.mapper

import io.timemates.backend.data.authorization.cache.entities.CacheAuthorization
import io.timemates.backend.data.authorization.db.entities.DbAuthorization

class CacheAuthorizationsMapper {
    fun dbToCacheAuthorization(auth: DbAuthorization): CacheAuthorization = with(auth) {
        CacheAuthorization(
            userId = userId,
            accessHash = accessHash,
            refreshAccessHash = refreshAccessHash,
            permissions = dbPermissionsToCachePermissions(permissions),
            expiresAt = expiresAt,
            createdAt = createdAt,
        )
    }

    private fun dbPermissionsToCachePermissions(
        permissions: DbAuthorization.Permissions
    ): CacheAuthorization.Permissions = with(permissions) {
        CacheAuthorization.Permissions(
            authorization = authorization.toCacheGrantLevel(),
            users = users.toCacheGrantLevel(),
            files = files.toCacheGrantLevel(),
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