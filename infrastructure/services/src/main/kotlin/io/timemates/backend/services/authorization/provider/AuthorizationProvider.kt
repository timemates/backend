package io.timemates.backend.services.authorization.provider

import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.usecases.GetAuthorizationUseCase

class AuthorizationProvider(
    private val getAuthorization: GetAuthorizationUseCase,
) {
    suspend fun provide(accessHash: AccessHash): Authorization? =
        (getAuthorization.execute(accessHash) as? GetAuthorizationUseCase.Result.Success)?.authorization
}