package io.timemates.backend.validation

import com.timemates.backend.validation.ValidationScope

fun testValidationScope(block: context(ValidationScope) () -> Unit) {
    val scope = ValidationScope { message ->
        throw AssertionError("Validation failed: $message.")
    }
}