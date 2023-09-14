package io.timemates.backend.rsocket.features.requests

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileRequest(
    val bytes: Flow<ByteArray>,
)