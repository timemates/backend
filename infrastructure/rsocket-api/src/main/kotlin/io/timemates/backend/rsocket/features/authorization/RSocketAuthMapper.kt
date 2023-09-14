package io.timemates.backend.rsocket.features.authorization

import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.AuthorizationsScope
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.files.types.FilesScope
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketMapper
import io.timemates.backend.serializable.types.authorization.SerializableAuthorization
import io.timemates.backend.serializable.types.authorization.SerializableAuthorizationScope
import io.timemates.backend.serializable.types.authorization.SerializableClientMetadata
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.UsersScope

class RSocketAuthMapper : RSocketMapper {
    fun toDomainSerializableClientMetadata(metadata: SerializableClientMetadata, ipAddress: String): ClientMetadata {
        return ClientMetadata(
            clientName = ClientName.createOrFail(metadata.clientName),
            clientVersion = ClientVersion.createOrFail(metadata.clientVersion),
            clientIpAddress = ClientIpAddress.createOrFail(ipAddress),
        )
    }

    fun fromDomainSerializableClientMetadata(metadata: ClientMetadata): SerializableClientMetadata = with(metadata) {
        return SerializableClientMetadata(
            clientVersion = clientVersion.double,
            clientName = clientName.string,
        )
    }

    fun fromDomainSerializableAuthorization(authorization: Authorization): SerializableAuthorization = with(authorization) {
        return SerializableAuthorization(
            userId = userId.long,
            accessHash = accessHash.string,
            refreshAccessHash = refreshAccessHash.string,
            scopes = fromDomainAuthScope(scopes),
            expiresAt = expiresAt.inMilliseconds,
            createdAt = createdAt.inMilliseconds,
            clientMetadata = fromDomainSerializableClientMetadata(clientMetadata),
        )
    }

    private fun fromDomainAuthScope(scopes: List<Scope>): List<SerializableAuthorizationScope> = scopes.map { scope ->
        when (scope) {
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
}