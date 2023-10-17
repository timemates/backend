package io.timemates.backend.serializable.types.authorization

import io.timemates.api.rsocket.serializable.types.authorization.SerializableAuthorizationScope
import io.timemates.backend.authorization.types.AuthorizationsScope
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.files.types.FilesScope
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.UsersScope

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