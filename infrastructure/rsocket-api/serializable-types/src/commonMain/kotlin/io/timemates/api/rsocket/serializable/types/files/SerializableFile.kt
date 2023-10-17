package io.timemates.api.rsocket.serializable.types.files

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