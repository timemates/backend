package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable
import org.tomadoro.backend.application.types.value.FileId

@Serializable
sealed interface SetAvatarResult {
    @Serializable
    class Success(val fileId: FileId) : SetAvatarResult
}