package io.timemates.backend.rsocket.internal

import io.rsocket.kotlin.RSocketError
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.internal.markers.RSocketService


/**
 * Throws a custom RSocket error based on provided failure code and message. Intended
 * to be used only in rSocket service context.
 *
 * @param failureCode Failure code for the error.
 * @param message Detailing the reason for failure.
 * @return Never returns; always throws an exception.
 *
 * @see RSocketFailureCode
 */
context (RSocketService)
fun failRequest(failureCode: RSocketFailureCode, message: String): Nothing {
    throw RSocketError.Custom(failureCode.code, message)
}