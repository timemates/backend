package io.timemates.backend.rsocket.features.files.requests

import io.timemates.backend.serializable.types.files.SerializableFileType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileRequest(
    val fileType: SerializableFileType,
    val bytes: Flow<ByteArray>,
) {
    @Serializable
    data class Metadata(val fileName: String, val fileType: SerializableFileType)

    @Serializable
    data class Response(val fileId: String)
}