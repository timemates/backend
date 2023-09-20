package io.timemates.backend.testing.validation

import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.ValidationFailureHandler
import org.jetbrains.annotations.TestOnly

val ValidationFailureHandler.Companion.THROWS_ASSERTION_ERROR by lazy {
    ValidationFailureHandler { failure ->
        throw AssertionError(failure.string)
    }
}

/**
 * A function for testing to pass / fail test where [ValidationFailureHandler]
 * is needed.
 *
 * @throws AssertionError if the validation fails.
 */
@TestOnly
@Throws(AssertionError::class)
fun <T, W> SafeConstructor<T, W>.createOrAssert(
    w: W,
): T = with(ValidationFailureHandler.THROWS_ASSERTION_ERROR) {
    create(w)
}
