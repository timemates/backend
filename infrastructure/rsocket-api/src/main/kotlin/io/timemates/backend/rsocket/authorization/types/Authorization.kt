package io.timemates.backend.rsocket.authorization.types

import io.timemates.backend.features.authorization.Scope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Authorization(
    val userId: Long,
    val accessHash: String,
    val refreshAccessHash: String,
    val scopes: List<Scope>,
    val expiresAt: Long,
    val createdAt: Long,
    val clientMetadata: ClientMetadata,
) {
    @Serializable
    sealed interface Scope {
        @SerialName("super")
        data object All : Scope

        @SerialName("auth_read")
        data object AuthRead : Scope

        @SerialName("auth_write")
        data object AuthWrite : Scope

        @SerialName("users_read")
        data object UsersRead : Scope

        @SerialName("users_write")
        data object UsersWrite : Scope

        @SerialName("timers_read")
        data object TimersRead : Scope

        @SerialName("timers_write")
        data object TimersWrite : Scope

        @SerialName("files_read")
        data object FilesRead : Scope

        @SerialName("files_write")
        data object FilesWrite : Scope
    }
}