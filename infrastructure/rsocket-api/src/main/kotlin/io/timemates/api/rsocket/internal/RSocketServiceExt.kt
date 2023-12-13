package io.timemates.api.rsocket.internal

import io.rsocket.kotlin.RSocketError
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import io.timemates.rsproto.server.RSocketService

/**
 * Used as a handler for validation inside RSocket requests.
 * failures and propagates the failure to the top of the hierarchy
 *.
 *
 * @property rSocketFailureHandler A handler for validation failures.
 * @see ValidationFailureHandler
 */
private val rSocketFailureHandler = ValidationFailureHandler { message ->
    throw RSocketError.Invalid(message.string)
}

/**
 * Creates an instance of type [T] using the provided value [value] and returns it.
 * This method is used to create a valid instance of [T] by invoking the [create] method
 * of [SafeConstructor] with the given [value].
 *
 * @param value The value used to create the instance of type [T].
 * @return The created instance of type [T].
 * @see SafeConstructor
 */
context(RSocketService)
internal fun <T, W> SafeConstructor<T, W>.createOrFail(value: W): T {
    return with(rSocketFailureHandler) {
        create(value)
    }
}

object Request {
    context(RSocketService)
    internal fun userAccessHash(): String = TODO()
}