package io.timemates.backend.repositories

import io.timemates.backend.types.value.UnixTime

interface AuthorizationsRepository {
    /**
     * Creates authorization for [userId] with [accessToken] until [expiresAt] time.
     */
    suspend fun create(
        userId: UsersRepository.UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
        expiresAt: UnixTime
    )

    suspend fun remove(accessToken: AccessToken): Boolean

    /**
     * Gets authorization by [accessToken]
     */
    suspend fun get(
        accessToken: AccessToken,
        currentTime: UnixTime
    ): Authorization?

    /**
     * Gets authorizations by [userId]
     */
    suspend fun getList(userId: UsersRepository.UserId): List<Authorization>

    /**
     * Renews [AccessToken] by [refreshToken] in [Authorization].
     */
    suspend fun renew(
        refreshToken: RefreshToken,
        accessToken: AccessToken,
        expiresAt: UnixTime
    ): Authorization?

    @JvmInline
    value class AccessToken(val string: String) {
        companion object {
            const val SIZE = 128
        }
    }

    @JvmInline
    value class RefreshToken(val string: String) {
        companion object {
            const val SIZE = 128
        }
    }

    data class Authorization(
        val userId: UsersRepository.UserId,
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
        val expiresAt: UnixTime
    )
}