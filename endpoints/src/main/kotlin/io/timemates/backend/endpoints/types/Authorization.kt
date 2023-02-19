package io.timemates.backend.endpoints.types

import io.timemates.backend.endpoints.types.value.*
import io.timemates.backend.endpoints.types.value.Milliseconds
import io.timemates.backend.endpoints.types.value.RefreshToken
import io.timemates.backend.repositories.AuthorizationsRepository
import kotlinx.serialization.Serializable

@Serializable
data class Authorization(
    val userId: UserId,
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
    val expiresAt: Milliseconds
)

internal fun AuthorizationsRepository.Authorization.serializable() =
    Authorization(
        userId.serializable(),
        accessToken.serializable(),
        refreshToken.serializable(),
        expiresAt.serializable()
    )