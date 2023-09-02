package io.timemates.backend.rsocket.authorization.providers

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.backend.rsocket.internal.markers.RSocketService

context (RSocketService)
suspend inline fun <reified T : Scope, R> provideAuthorizationContext(
    constraint: (List<Scope>) -> Boolean = { scopes -> scopes.any { it is T || it is Scope.All } },
    block: context(AuthorizedContext<T>) () -> R,
): R {
    TODO()
}