package io.timemates.api.rsocket.serializable.types.authorization

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