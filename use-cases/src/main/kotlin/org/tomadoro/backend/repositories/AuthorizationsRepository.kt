package org.tomadoro.backend.repositories

import org.tomadoro.backend.domain.value.DateTime

interface AuthorizationsRepository {
    /**
     * Creates authorization for [userId] with [accessToken] until [expiresAt] time.
     */
    suspend fun create(
        userId: UsersRepository.UserId,
        accessToken: AccessToken,
        refreshToken: RefreshToken,
        expiresAt: DateTime
    )

    /**
     * Removes authorization for [userId] where [accessToken].
     */
    suspend fun remove(accessToken: AccessToken): Boolean

    /**
     * Gets authorization by [accessToken]
     */
    suspend fun get(
        accessToken: AccessToken,
        currentTime: DateTime
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
        expiresAt: DateTime
    ): Authorization?

    @JvmInline
    value class AccessToken(val string: String)

    @JvmInline
    value class RefreshToken(val string: String)

    data class Authorization(
        val userId: UsersRepository.UserId,
        val accessToken: AccessToken,
        val refreshToken: RefreshToken,
        val expiresAt: DateTime
    )
}