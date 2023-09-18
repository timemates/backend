package io.timemates.backend.rsocket.internal

import io.rsocket.kotlin.RSocketError
import io.timemates.backend.rsocket.internal.markers.RSocketMarker
import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler

/**
 * @throws [RSocketError.Invalid] on service-mapping operations to propagate type-safe
 * failures.
 */
internal val ValidationFailureHandler.Companion.THROWS_RSOCKET_FAILURE: ValidationFailureHandler by lazy {
    ValidationFailureHandler { failure ->
        throw RSocketError.Invalid(failure.string)
    }
}


context(RSocketMarker)
@Throws(RSocketError.Invalid::class)
internal fun <T, W> SafeConstructor<T, W>.createOrFail(
    value: W,
): T = with(ValidationFailureHandler.THROWS_RSOCKET_FAILURE) {
    create(value)
}