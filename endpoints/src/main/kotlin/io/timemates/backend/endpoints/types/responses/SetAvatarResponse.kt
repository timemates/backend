package io.timemates.backend.endpoints.types.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.timemates.backend.endpoints.types.value.FileId

@Serializable
sealed interface SetAvatarResponse {
    @SerialName("success")
    @Serializable
    class Success(@SerialName("file_id") val fileId: FileId) : SetAvatarResponse
}