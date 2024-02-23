package org.timemates.api.rsocket.internal

import org.timemates.rsproto.server.RSocketService
import kotlinx.coroutines.currentCoroutineContext
import org.timemates.api.rsocket.auth.AuthInterceptor
import org.timemates.backend.auth.domain.usecases.GetAuthorizationUseCase
import org.timemates.backend.foundation.authorization.AuthDelicateApi
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.foundation.authorization.Scope
import org.timemates.backend.foundation.authorization.types.AuthorizedId
import org.timemates.backend.types.auth.value.AccessHash

/**
 * Executes the provided block of code within an authorized context.
 *
 * @param block The code block to be executed within the authorized context.
 * @return The result of executing the code block.
 */
context(RSocketService)
internal suspend inline fun <reified T : Scope> getAuthorization(
    constraint: (List<Scope>) -> Boolean = { scopes -> scopes.any { it is T || it is Scope.All } },
): Authorized<T> {
    val authInfo = currentCoroutineContext()[AuthInterceptor.Data] ?: unauthorized()
    val accessHash = authInfo.accessHash ?: unauthorized()

    return (authInfo.authorizationProvider.execute(AccessHash.createOrFail(accessHash)) as? GetAuthorizationUseCase.Result.Success)
        ?.authorization
        ?.takeIf { constraint(it.scopes) }
        ?.let {
            @OptIn(AuthDelicateApi::class)
            Authorized(AuthorizedId(it.userId.long))
        } ?: unauthorized()
}