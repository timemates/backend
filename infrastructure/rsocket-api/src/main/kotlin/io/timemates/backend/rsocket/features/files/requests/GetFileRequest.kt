package io.timemates.backend.rsocket.features.files.requests

import io.timemates.backend.serializable.types.files.SerializableFileType
import kotlinx.serialization.Serializable

@Serializable
data class GetFileRequest(
    val fileId: String,
) {
    @Serializable
    sealed interface Response {
        data class Metadata(
            val fileType: SerializableFileType,
        ) : Response

        @JvmInline
        value class Chunk(val bytes: ByteArray) : Response
    }
}