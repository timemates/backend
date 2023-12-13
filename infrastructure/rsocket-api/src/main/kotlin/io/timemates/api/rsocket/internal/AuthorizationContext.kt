package io.timemates.api.rsocket.internal

import io.timemates.backend.features.authorization.AuthorizedContext
import io.timemates.backend.features.authorization.Scope
import io.timemates.rsproto.server.RSocketService

/**
 * Executes the provided block of code within an authorized context.
 *
 * @param block The code block to be executed within the authorized context.
 * @return The result of executing the code block.
 */
context(RSocketService)
internal suspend inline fun <T : Scope, R> authorized(block: AuthorizedContext<T>.() -> R): R {
    TODO()
}