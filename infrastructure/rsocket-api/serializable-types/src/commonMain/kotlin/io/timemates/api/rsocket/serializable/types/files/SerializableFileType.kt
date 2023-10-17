package io.timemates.api.rsocket.serializable.types.files

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SerializableFileType {
    @SerialName("image")
    IMAGE,
}