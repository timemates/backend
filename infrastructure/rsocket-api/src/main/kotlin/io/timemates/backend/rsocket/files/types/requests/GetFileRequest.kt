package io.timemates.backend.rsocket.files.types.requests

import kotlinx.serialization.Serializable

@Serializable
data class GetFileRequest(
    val fileId: String,
)