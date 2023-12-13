package io.timemates.api.rsocket.internal

import io.ktor.http.*
import io.rsocket.kotlin.RSocketError
import io.timemates.rsproto.server.RSocketService

context(RSocketService)
internal fun attemptsExceeded(): Nothing = throw ApiFailure.TooManyRequests

context(RSocketService)
internal fun tooManyRequests(): Nothing = throw ApiFailure.TooManyRequests

context(RSocketService)
internal fun notFound(): Nothing = throw ApiFailure.NotFound

context(RSocketService)
internal fun internalFailure(): Nothing = throw ApiFailure.InternalServerError

context(RSocketService)
internal fun alreadyExists(): Nothing = throw ApiFailure.AlreadyExists

context(RSocketService)
internal fun noAccess(): Nothing = throw ApiFailure.NoAccess

context(RSocketService)
internal fun unauthorized(): Nothing = throw ApiFailure.Unauthorized

internal object ApiFailure {
    /**
     * Variable for custom RSocket error when the number of attempts exceeds a limit.
     *
     * This variable holds a custom RSocket error object of type [RSocketError.Custom].
     * The error code is set to [HttpStatusCode.TooManyRequests.value] (429) and the error message is set to "Too many requests.".
     *
     * @see RSocketError.Custom
     * @see HttpStatusCode.TooManyRequests
     */
    val TooManyRequests: RSocketError.Custom
        get() = RSocketError.Custom(HttpStatusCode.TooManyRequests.value, "Too many requests.")

    /**
     * Represents an Internal Server Error.
     *
     * This variable is of type [RSocketError.Custom] and it is used to indicate an internal server error.
     * The HTTP status code for this error is 500 (InternalServerError) and the corresponding error message is "Internal server error.".
     *
     * @see RSocketError.Custom
     */
    val InternalServerError: RSocketError.Custom
        get() = RSocketError.Custom(HttpStatusCode.InternalServerError.value, "Internal server error.")

    /**
     * Represents the "Not Found" error.
     *
     * The `NotFound` variable is of type `RSocketError.Custom` and can be used to create an instance
     * of a custom RSocket error with a status code of 404 (Not Found) and a default message of "Not found.".
     */
    val NotFound: RSocketError.Custom
        get() = RSocketError.Custom(HttpStatusCode.NotFound.value, "Not found.")

    /**
     * Custom RSocketError used for signaling that a conflict has occurred and the resource already exists.
     */
    val AlreadyExists: RSocketError.Custom
        get() = RSocketError.Custom(HttpStatusCode.Conflict.value, "Already exists.")

    /**
     * Represents a custom RSocket error indicating that there is no access to a given resource or operation.
     *
     * This error is returned with the HTTP status code 403 (Forbidden).
     */
    val NoAccess: RSocketError.Custom
        get() = RSocketError.Custom(HttpStatusCode.Forbidden.value, "No access to given resource or operation.")

    /**
     * Represents unauthorized failure in requests.
     */
    val Unauthorized: RSocketError.Custom
        get() = RSocketError.Custom(HttpStatusCode.Unauthorized.value, "Unauthorized.")
}