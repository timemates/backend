package io.timemates.backend.serializable.types.authorization

import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorization
import io.timemates.backend.authorization.types.Authorization

fun Authorization.serializable(): SerializableAuthorization = SerializableAuthorization(
    userId = userId.long,
    accessHash = accessHash.string,
    refreshAccessHash = refreshAccessHash.string,
    scopes = scopes.map { it.serializable() },
    expiresAt = expiresAt.inMilliseconds,
    createdAt = createdAt.inMilliseconds,
    clientMetadata = clientMetadata.serializable()
)