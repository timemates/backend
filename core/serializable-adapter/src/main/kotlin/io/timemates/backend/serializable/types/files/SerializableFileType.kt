package io.timemates.backend.serializable.types.files

import io.timemates.api.rsocket.serializable.types.files.SerializableFileType
import io.timemates.backend.files.types.FileType

fun FileType.serializable(): SerializableFileType = when (this) {
    FileType.IMAGE -> SerializableFileType.IMAGE
}