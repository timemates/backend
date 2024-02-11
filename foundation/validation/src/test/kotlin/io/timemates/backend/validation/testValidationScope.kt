package io.timemates.backend.validation

fun testValidationScope(block: context(ValidationFailureHandler) () -> Unit) {
    val scope = ValidationFailureHandler { message ->
        throw AssertionError("Validation failed: $message.")
    }
}