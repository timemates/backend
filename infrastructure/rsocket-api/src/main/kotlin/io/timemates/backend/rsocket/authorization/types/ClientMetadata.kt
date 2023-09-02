package io.timemates.backend.rsocket.authorization.types

import kotlinx.serialization.Serializable

@Serializable
data class ClientMetadata(
    val clientName: String,
    val clientVersion: Double,
)