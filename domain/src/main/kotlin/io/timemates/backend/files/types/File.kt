package io.timemates.backend.files.types

import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.types.value.ImageSize

sealed class File {
    abstract val fileId: FileId

    /**
     * File with image.
     */
    data class Image(override val fileId: FileId) : File()
}