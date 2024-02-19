package org.timemates.backend.types.auth.exceptions

/**
 * Represents an exception related to authorization issues.
 *
 * @param message A detailed message describing the authorization exception.
 */
sealed class AuthorizationException(message: String) : Exception(message)

/**
 * Exception thrown when no access hash is provided during authorization.
 */
class NoAccessHashException : AuthorizationException(
    message = "No access hash was provided."
)

/**
 * Exception thrown when the provided token has expired, been terminated, or is invalid.
 */
class InvalidAccessHashException : AuthorizationException(
    message = "Your token has expired, been terminated, or is simply invalid."
)
