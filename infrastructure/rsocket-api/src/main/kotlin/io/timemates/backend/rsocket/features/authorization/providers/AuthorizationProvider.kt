package io.timemates.backend.rsocket.features.authorization.providers

import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.usecases.GetAuthorizationUseCase
import io.timemates.backend.features.authorization.Authorized
import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.features.authorization.authorizationProvider
import io.timemates.backend.features.authorization.types.AuthorizedId
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.interceptors.AuthorizableRouteContext
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import kotlin.coroutines.coroutineContext

/**
 * A class responsible for providing authorization information based on an access hash.
 *
 * @property useCase The use case for retrieving authorization data.
 */
class AuthorizationProvider(private val useCase: GetAuthorizationUseCase) {

    /**
     * Provides authorization information based on an access hash.
     *
     * @param accessHash The access hash used to retrieve authorization information.
     * @return The Authorization object if authorization is valid, or null if not.
     */
    context (RSocketService)
    suspend fun provide(accessHash: String): Authorization? {
        return when (val result = useCase.execute(AccessHash.createOrFail(accessHash))) {
            is GetAuthorizationUseCase.Result.Success -> result.authorization
            is GetAuthorizationUseCase.Result.NotFound -> null
        }
    }
}

/**
 * Provides an authorization context within a coroutine. This function allows you to specify a constraint for the
 * authorization scopes, and it checks if the provided access hash is valid and meets the constraint before proceeding.
 *
 * @param T The type of authorization scope being checked.
 * @param R The return type of the block.
 * @param constraint A function that defines the constraint for authorization scopes.
 * @param block The block of code to be executed if authorization is successful.
 * @return The result of executing the provided block.
 */
context (RSocketService)
suspend inline fun <reified T : Scope, R> provideAuthorizationContext(
    constraint: (List<Scope>) -> Boolean = { scopes -> scopes.any { it is T || it is Scope.All } },
    block: context(AuthorizedContext<T>) () -> R,
): R {
    val (accessHash, _, provider) = coroutineContext[AuthorizableRouteContext]!!

    return authorizationProvider(
        provider = {
            provider.provide(accessHash)?.takeIf {
                constraint(it.scopes)
            }?.let { Authorized(AuthorizedId(it.authorizedId.long), it.scopes) }
        },
        onFailure = {
            failRequest(
                failureCode = RSocketFailureCode.UNAUTHORIZED,
                message = "Access hash is invalid or expired.",
            )
        },
        block = block,
    )
}