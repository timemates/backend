package org.timemates.backend.types.auth.metadata

data class ClientMetadata(
    val clientName: org.timemates.backend.types.auth.metadata.value.ClientName,
    val clientVersion: org.timemates.backend.types.auth.metadata.value.ClientVersion,
    val clientIpAddress: org.timemates.backend.types.auth.metadata.value.ClientIpAddress,
)