package org.timemates.api.rsocket.internal

import io.rsocket.kotlin.RSocketError
import io.timemates.rsproto.server.RSocketService
import kotlinx.coroutines.currentCoroutineContext
import org.timemates.api.rsocket.auth.AuthInterceptor
import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.createOr

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
    return createOr(value) { throwable ->
        throw RSocketError.Invalid(throwable.message ?: "Validation failed.")
    }
}

object Request {
    context(RSocketService)
    @JvmStatic
    internal suspend fun userAccessHash(): String? = currentCoroutineContext()[AuthInterceptor.Data]?.accessHash
}