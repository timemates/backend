package io.timemates.backend.testing.validation

import com.timemates.backend.validation.ValidationScope
import com.timemates.backend.validation.withValidation
import org.jetbrains.annotations.TestOnly

/**
 * A function for testing to pass / fail test where [ValidationScope]
 * is needed.
 *
 * @param block A block of code to be executed within the validation scope.
 *
 * @throws AssertionError if the validation fails.
 */
@TestOnly
@Throws(AssertionError::class)
fun testValidationScope(block: context(ValidationScope) () -> Unit) {
    withValidation(
        handler = {
            message -> throw AssertionError("Validation failed: $message")
        },
        block = block,
    )
}