package io.timemates.backend.serializable.types.files

import io.timemates.api.rsocket.serializable.types.files.SerializableFile
import io.timemates.backend.files.types.File

fun File.serializable(): SerializableFile {
    return when (this) {
        is File.Image -> SerializableFile.Image(fileId.string)
    }
}