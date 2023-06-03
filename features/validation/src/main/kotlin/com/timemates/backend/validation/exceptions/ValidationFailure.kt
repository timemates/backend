package com.timemates.backend.validation.exceptions

public class ValidationFailure(
    message: String,
) : Exception("Validation failed with message: $message")