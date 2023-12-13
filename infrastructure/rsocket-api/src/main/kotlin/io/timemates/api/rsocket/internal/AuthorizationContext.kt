package io.timemates.api.rsocket.internal

import io.timemates.api.rsocket.auth.AuthInterceptor
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.usecases.GetAuthorizationUseCase
import io.timemates.backend.features.authorization.Authorized
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.features.authorization.authorizationProvider
import io.timemates.backend.features.authorization.types.AuthorizedId
import io.timemates.rsproto.server.RSocketService
import kotlinx.coroutines.currentCoroutineContext

/**
 * Executes the provided block of code within an authorized context.
 *
 * @param block The code block to be executed within the authorized context.
 * @return The result of executing the code block.
 */
context(RSocketService)
internal suspend inline fun <reified T : Scope, R> authorized(
    constraint: (List<Scope>) -> Boolean = { scopes -> scopes.any { it is T || it is Scope.All } },
    block: AuthorizedContext<T>.() -> R,
): R {
    val authInfo = currentCoroutineContext()[AuthInterceptor.Data] ?: unauthorized()
    val accessHash = authInfo.accessHash ?: unauthorized()

    return authorizationProvider(
        provider = {
            authInfo.authorizationProvider.execute(AccessHash.createOrFail(accessHash))
                .let { (it as? GetAuthorizationUseCase.Result.Success)?.authorization ?: unauthorized() }
                .takeIf { constraint(it.scopes) }
                ?.let { Authorized(AuthorizedId(it.userId.long), scopes = it.scopes) }
        },
        onFailure = { unauthorized() },
        block = block,
    )
}