package io.timemates.backend.rsocket.common

import io.ktor.http.*

/**
 * Represents RSocket failure codes with their associated integer values.
 *
 * @property code The integer value associated with the RSocket failure code.
 */
@JvmInline
value class RSocketFailureCode(val code: Int) {
    companion object {
        /**
         * Represents the internal server error RSocket failure code.
         */
        val INTERNAL_SERVER_ERROR = RSocketFailureCode(HttpStatusCode.InternalServerError.value)

        /**
         * Represents the invalid argument RSocket failure code.
         */
        val INVALID_ARGUMENT = RSocketFailureCode(HttpStatusCode.BadRequest.value)

        /**
         * Represents the failed precondition RSocket failure code.
         */
        val FAILED_PRECONDITION = RSocketFailureCode(HttpStatusCode.PreconditionFailed.value)

        /**
         * Represents the not found RSocket failure code.
         */
        val NOT_FOUND = RSocketFailureCode(HttpStatusCode.NotFound.value)

        /**
         * Represents the too many requests RSocket failure code.
         */
        val TOO_MANY_REQUESTS = RSocketFailureCode(HttpStatusCode.TooManyRequests.value)

        /**
         * Represents the already exists RSocket failure code.
         */
        val ALREADY_EXISTS = RSocketFailureCode(HttpStatusCode.Conflict.value)

        /**
         * Represents the unauthorized RSocket failure code.
         */
        val UNAUTHORIZED = RSocketFailureCode(HttpStatusCode.Unauthorized.value)
    }
}
