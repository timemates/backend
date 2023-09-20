package io.timemates.backend.serializable.types.authorization

import io.timemates.backend.authorization.types.metadata.ClientMetadata
import kotlinx.serialization.Serializable

@Serializable
data class SerializableClientMetadata(
    val clientName: String,
    val clientVersion: Double,
)

fun ClientMetadata.serializable(): SerializableClientMetadata = SerializableClientMetadata(
    clientName = clientName.string,
    clientVersion = clientVersion.double,
)