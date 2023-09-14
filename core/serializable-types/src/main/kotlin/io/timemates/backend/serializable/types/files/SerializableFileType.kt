package io.timemates.backend.serializable.types.files

import io.timemates.backend.files.types.FileType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SerializableFileType {
    @SerialName("image") IMAGE,
}

fun FileType.serializable(): SerializableFileType = when (this) {
    FileType.IMAGE -> SerializableFileType.IMAGE
}