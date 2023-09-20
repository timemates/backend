package io.timemates.backend.serializable.types.authorization

import io.timemates.backend.authorization.types.Authorization
import kotlinx.serialization.Serializable

@Serializable
data class SerializableAuthorization(
    val userId: Long,
    val accessHash: String,
    val refreshAccessHash: String,
    val scopes: List<SerializableAuthorizationScope>,
    val expiresAt: Long,
    val createdAt: Long,
    val clientMetadata: SerializableClientMetadata,
)

fun Authorization.serializable(): SerializableAuthorization = SerializableAuthorization(
    userId = userId.long,
    accessHash = accessHash.string,
    refreshAccessHash = refreshAccessHash.string,
    scopes = scopes.map { it.serializable() },
    expiresAt = expiresAt.inMilliseconds,
    createdAt = createdAt.inMilliseconds,
    clientMetadata = clientMetadata.serializable()
)