package io.timemates.backend.testing.validation

import com.timemates.backend.validation.ValidationFailureHandler
import com.timemates.backend.validation.validation
import org.jetbrains.annotations.TestOnly

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
fun testValidationScope(block: context(ValidationFailureHandler) () -> Unit) {
    validation(
        handler = {
            message -> throw AssertionError("Validation failed: $message")
        },
        block = block,
    )
}