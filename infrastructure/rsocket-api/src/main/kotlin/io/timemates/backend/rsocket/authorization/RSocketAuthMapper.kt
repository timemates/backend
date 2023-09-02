package io.timemates.backend.rsocket.authorization

import io.timemates.backend.authorization.types.AuthorizationsScope
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.files.types.FilesScope
import io.timemates.backend.rsocket.authorization.types.Authorization
import io.timemates.backend.rsocket.authorization.types.ClientMetadata
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.markers.RSocketMapper
import io.timemates.backend.timers.types.TimersScope
import io.timemates.backend.users.types.UsersScope
import io.timemates.backend.authorization.types.Authorization as DomainAuthorization
import io.timemates.backend.authorization.types.metadata.ClientMetadata as DomainMetadata

class RSocketAuthMapper : RSocketMapper {
    fun toDomainClientMetadata(metadata: ClientMetadata, ipAddress: String): DomainMetadata {
        return DomainMetadata(
            clientName = ClientName.createOrFail(metadata.clientName),
            clientVersion = ClientVersion.createOrFail(metadata.clientVersion),
            clientIpAddress = ClientIpAddress.createOrFail(ipAddress),
        )
    }

    fun fromDomainClientMetadata(metadata: DomainMetadata): ClientMetadata = with(metadata) {
        return ClientMetadata(
            clientVersion = clientVersion.double,
            clientName = clientName.string,
        )
    }

    fun fromDomainAuthorization(authorization: DomainAuthorization): Authorization = with(authorization) {
        return Authorization(
            userId = userId.long,
            accessHash = accessHash.string,
            refreshAccessHash = refreshAccessHash.string,
            scopes = fromDomainAuthScope(scopes),
            expiresAt = expiresAt.inMilliseconds,
            createdAt = createdAt.inMilliseconds,
            clientMetadata = fromDomainClientMetadata(clientMetadata),
        )
    }

    private fun fromDomainAuthScope(scopes: List<Scope>): List<Authorization.Scope> = scopes.map { scope ->
        when (scope) {
            is Scope.All -> Authorization.Scope.All
            is UsersScope.Write -> Authorization.Scope.UsersWrite
            is UsersScope.Read -> Authorization.Scope.UsersRead
            is AuthorizationsScope.Write -> Authorization.Scope.AuthWrite
            is AuthorizationsScope.Read -> Authorization.Scope.AuthRead
            is FilesScope.Write -> Authorization.Scope.FilesWrite
            is FilesScope.Read -> Authorization.Scope.FilesRead
            is TimersScope.Write -> Authorization.Scope.FilesWrite
            is TimersScope.Read -> Authorization.Scope.TimersRead

            else -> error("Unexpected type of authorization scope")
        }
    }
}