package io.timemates.backend.rsocket.features.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetFileRequest(
    val fileId: String,
)