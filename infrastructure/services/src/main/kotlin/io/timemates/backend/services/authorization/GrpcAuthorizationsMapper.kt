package io.timemates.backend.services.authorization

import io.timemates.api.authorizations.types.AuthorizationKt
import io.timemates.api.authorizations.types.AuthorizationOuterClass
import io.timemates.api.authorizations.types.authorization
import io.timemates.backend.authorization.types.Authorization

class GrpcAuthorizationsMapper {
    fun toGrpcAuthorization(authorization: Authorization): AuthorizationOuterClass.Authorization {
        return authorization {
            accessHash = AuthorizationKt.hash {
                value = authorization.accessHash.string
                expiresAt = authorization.expiresAt.inMilliseconds
            }
            refreshHash = AuthorizationKt.hash {
                value = authorization.refreshAccessHash.string
                expiresAt = authorization.expiresAt.inMilliseconds
            }
            generationTime = authorization.createdAt.inMilliseconds
        }
    }
}