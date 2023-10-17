package io.timemates.backend.serializable.types.authorization

import io.timemates.api.rsocket.serializable.types.authorization.SerializableClientMetadata
import io.timemates.backend.authorization.types.metadata.ClientMetadata

fun ClientMetadata.serializable(): SerializableClientMetadata = SerializableClientMetadata(
    clientName = clientName.string,
    clientVersion = clientVersion.double,
)