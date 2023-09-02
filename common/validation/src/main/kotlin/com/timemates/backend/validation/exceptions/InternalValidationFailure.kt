package com.timemates.backend.validation.exceptions

internal class InternalValidationFailure(
    message: String,
) : Exception("Validation failed with message: $message")