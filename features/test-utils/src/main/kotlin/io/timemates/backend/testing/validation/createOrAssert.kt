package io.timemates.backend.testing.validation

import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler
import org.jetbrains.annotations.TestOnly

val ValidationFailureHandler.Companion.ALWAYS_ASSERTION_ERROR by lazy {
    ValidationFailureHandler { failure ->
        throw AssertionError(failure.string)
    }
}

/**
 * A function for testing to pass / fail test where [ValidationFailureHandler]
 * is needed.
 *
 * @param block A block of code to be executed within the validation scope.
 *
 * @throws AssertionError if the validation fails.
 */
@TestOnly
@Throws(AssertionError::class)
fun <T, W> SafeConstructor<T, W>.createOrAssert(
    w: W,
): T = with(ValidationFailureHandler.ALWAYS_ASSERTION_ERROR) {
    create(w)
}
