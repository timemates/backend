package io.timemates.backend.services.authorization.context

import io.grpc.Status
import io.grpc.StatusException
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.features.authorization.Authorized
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.features.authorization.authorizationProvider
import io.timemates.backend.features.authorization.types.AuthorizedId
import io.timemates.backend.services.authorization.interceptor.AuthorizationContext
import io.timemates.backend.services.authorization.interceptor.AuthorizationInterceptor
import io.timemates.backend.services.common.validation.createOrStatus
import kotlin.coroutines.coroutineContext

/**
 * Provides an authorization context based on the access token provided in the request headers.
 * The context is only provided if the access token is valid and the user has the necessary scopes.
 *
 * @param constraint a function that takes a list of scopes and returns a boolean indicating whether
 * the user has the necessary scopes.
 * @param block a suspend function that provides an authorized context.
 * @throws StatusException if the user is not authenticated or does not have the necessary scopes.
 */
@Throws(StatusException::class)
suspend inline fun <reified T : Scope, R> provideAuthorizationContext(
    constraint: (List<Scope>) -> Boolean = { scopes -> scopes.any { it is T || it is Scope.All } },
    block: context(AuthorizedContext<T>) () -> R,
): R {
    val authorizationContext = coroutineContext[AuthorizationContext]!!
    val accessHash = AccessHash.createOrStatus(authorizationContext?.accessHash ?: throw StatusException(Status.UNAUTHENTICATED))

    return authorizationProvider(
        provider = {
            authorizationContext.provider.provide(accessHash)?.takeIf {
                constraint(it.scopes)
            }?.let {
                Authorized(AuthorizedId(it.userId.long), scopes = it.scopes)
            }
        },
        onFailure = { throw StatusException(Status.UNAUTHENTICATED) },
        block = block,
    )
}