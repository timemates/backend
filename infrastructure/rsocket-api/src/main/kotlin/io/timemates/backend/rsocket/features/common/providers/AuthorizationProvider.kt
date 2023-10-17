package io.timemates.backend.rsocket.features.common.providers

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

class AuthorizationProvider(private val useCase: GetAuthorizationUseCase) {
    context(RSocketService)
    suspend fun provide(accessHash: String): Authorized? {
        return when (val result = useCase.execute(AccessHash.createOrFail(accessHash))) {
            is GetAuthorizationUseCase.Result.Success -> Authorized(
                authorizedId = AuthorizedId(result.authorization.userId.long),
                scopes = result.authorization.scopes,
            )

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
internal suspend inline fun <reified T : Scope, R> provideAuthorizationContext(
    constraint: (List<Scope>) -> Boolean = { scopes -> scopes.any { it is T || it is Scope.All } },
    block: AuthorizedContext<T>.() -> R,
): R {
    val (_, _, accessHash, provider) = coroutineContext[AuthorizableRouteContext]!!

    return authorizationProvider(
        provider = {
            accessHash?.let { provider.provide(it) }?.takeIf {
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