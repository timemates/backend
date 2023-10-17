package io.timemates.api.rsocket.serializable.types.authorization

import kotlinx.serialization.Serializable

@Serializable
data class SerializableClientMetadata(
    val clientName: String,
    val clientVersion: Double,
)