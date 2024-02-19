package org.timemates.backend.auth.domain.repositories

import com.timemates.backend.time.UnixTime
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.AuthorizationId
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.users.value.UserId

/**
 * An interface that provides methods to manage user authorizations.
 */
interface AuthorizationsRepository {

    /**
     * Creates a new authorization for a user with the specified [userId], [accessToken], [refreshToken],
     * [expiresAt], and [creationTime].
     *
     * @param userId The user ID associated with the authorization.
     * @param accessToken The access token used to authorize the user.
     * @param refreshToken The refresh token used to renew the access token.
     * @param expiresAt The time at which the access token expires.
     * @param creationTime The time at which the authorization was created.
     *
     * @return The ID of the newly created authorization.
     */
    suspend fun create(
        userId: UserId,
        accessToken: AccessHash,
        refreshToken: RefreshHash,
        expiresAt: UnixTime,
        creationTime: UnixTime,
        clientMetadata: ClientMetadata,
    ): AuthorizationId

    /**
     * Removes the authorization associated with the specified [accessToken].
     *
     * @param accessToken The access token used to authorize the user.
     *
     * @return `true` if the authorization was removed successfully, `false` otherwise.
     */
    suspend fun remove(accessToken: AccessHash): Boolean

    /**
     * Gets the authorization associated with the specified [accessToken].
     *
     * @param accessToken The access token used to authorize the user.
     * @param currentTime The time of the request.
     *
     * @return The authorization associated with the specified [accessToken], or `null` if not found.
     */
    suspend fun get(
        accessToken: AccessHash,
        currentTime: UnixTime,
    ): Authorization?

    /**
     * Gets a list of authorizations associated with the specified [userId], with pagination using [nextPageToken].
     *
     * @param userId The user ID associated with the authorizations.
     * @param nextPageToken The token used for pagination.
     *
     * @return A [Page] object containing the list of authorizations and the next page token.
     */
    suspend fun getList(userId: UserId, nextPageToken: PageToken?): Page<Authorization>

    /**
     * Renews the access token in the authorization associated with the specified [refreshToken], with the new
     * [expiresAt] time.
     *
     * @param refreshToken The refresh token used to renew the access token.
     * @param expiresAt The time at which the new access token expires.
     *
     * @return The renewed authorization, or `null` if not found.
     */
    suspend fun renew(
        refreshToken: RefreshHash,
        newAccessHash: AccessHash,
        newRefreshHash: RefreshHash,
        expiresAt: UnixTime,
    ): Authorization?
}
