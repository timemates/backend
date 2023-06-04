package io.timemates.backend.files.types

import io.timemates.backend.files.types.value.FileId

sealed class File {
    abstract val fileId: FileId

    /**
     * File with image.
     */
    data class Image(override val fileId: FileId) : File()
}