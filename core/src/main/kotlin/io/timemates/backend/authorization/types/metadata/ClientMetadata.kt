package io.timemates.backend.authorization.types.metadata

import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion

data class ClientMetadata(
    val clientName: ClientName,
    val clientVersion: ClientVersion,
    val clientIpAddress: ClientIpAddress,
)