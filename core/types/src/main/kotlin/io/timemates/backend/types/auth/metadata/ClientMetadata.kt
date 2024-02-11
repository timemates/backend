package io.timemates.backend.types.auth.metadata

import io.timemates.backend.types.auth.metadata.value.ClientIpAddress
import io.timemates.backend.types.auth.metadata.value.ClientName
import io.timemates.backend.types.auth.metadata.value.ClientVersion

data class ClientMetadata(
    val clientName: ClientName,
    val clientVersion: ClientVersion,
    val clientIpAddress: ClientIpAddress,
)