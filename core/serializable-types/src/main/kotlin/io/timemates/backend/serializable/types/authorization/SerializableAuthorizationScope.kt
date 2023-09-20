package io.timemates.backend.serializable.types.authorization

import io.timemates.backend.authorization.types.AuthorizationsScope
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.files.types.FilesScope
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.UsersScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface SerializableAuthorizationScope {
    @SerialName("super")
    data object All : SerializableAuthorizationScope

    @SerialName("auth:read")
    data object AuthRead : SerializableAuthorizationScope

    @SerialName("auth:write")
    data object AuthWrite : SerializableAuthorizationScope

    @SerialName("users:read")
    data object UsersRead : SerializableAuthorizationScope

    @SerialName("users:write")
    data object UsersWrite : SerializableAuthorizationScope

    @SerialName("timers:read")
    data object TimersRead : SerializableAuthorizationScope

    @SerialName("timers:write")
    data object TimersWrite : SerializableAuthorizationScope

    @SerialName("files:read")
    data object FilesRead : SerializableAuthorizationScope

    @SerialName("files:write")
    data object FilesWrite : SerializableAuthorizationScope
}

fun Scope.serializable(): SerializableAuthorizationScope {
    return when (this) {
        is Scope.All -> SerializableAuthorizationScope.All
        is UsersScope.Write -> SerializableAuthorizationScope.UsersWrite
        is UsersScope.Read -> SerializableAuthorizationScope.UsersRead
        is AuthorizationsScope.Write -> SerializableAuthorizationScope.AuthWrite
        is AuthorizationsScope.Read -> SerializableAuthorizationScope.AuthRead
        is FilesScope.Write -> SerializableAuthorizationScope.FilesWrite
        is FilesScope.Read -> SerializableAuthorizationScope.FilesRead
        is TimersScope.Write -> SerializableAuthorizationScope.FilesWrite
        is TimersScope.Read -> SerializableAuthorizationScope.TimersRead

        else -> error("Unexpected type of authorization scope")
    }
}