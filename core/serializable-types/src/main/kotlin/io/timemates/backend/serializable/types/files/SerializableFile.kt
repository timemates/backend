package io.timemates.backend.serializable.types.files

import io.timemates.backend.files.types.File
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SerializableFile {
    abstract val fileId: String

    /**
     * File with image.
     */
    @SerialName("image")
    data class Image(override val fileId: String) : SerializableFile()
}

fun File.serializable(): SerializableFile {
    return when (this) {
        is File.Image -> SerializableFile.Image(fileId.string)
    }
}