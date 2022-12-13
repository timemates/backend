package org.tomadoro.backend.application.results

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.FileId

@Serializable
sealed interface SetAvatarResult {
    @SerialName("success")
    @Serializable
    class Success(@SerialName("file_id") val fileId: FileId) : SetAvatarResult
}