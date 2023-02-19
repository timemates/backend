package io.timemates.backend.authorization.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.users.types.value.UserId

interface AuthorizationsRepository {
    /**
     * Creates authorization for [userId] with [accessToken] until [expiresAt] time.
     */
    suspend fun create(
        userId: UserId,
        accessToken: AccessHash,
        refreshToken: RefreshHash,
        expiresAt: UnixTime,
    )

    suspend fun remove(accessToken: AccessHash): Boolean

    /**
     * Gets authorization by [accessToken]
     */
    suspend fun get(
        accessToken: AccessHash,
        currentTime: UnixTime,
    ): Authorization?

    /**
     * Gets authorizations by [userId]
     */
    suspend fun getList(userId: UserId): List<Authorization>

    /**
     * Renews [AccessToken] by [refreshToken] in [Authorization].
     */
    suspend fun renew(
        refreshToken: RefreshHash,
        accessToken: AccessHash,
        expiresAt: UnixTime,
    ): Authorization?
}