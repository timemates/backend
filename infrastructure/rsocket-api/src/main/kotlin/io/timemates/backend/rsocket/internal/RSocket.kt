package io.timemates.backend.rsocket.internal

import io.rsocket.kotlin.RSocketError
import io.timemates.backend.rsocket.common.RSocketFailureCode
import io.timemates.backend.rsocket.internal.markers.RSocketService


context (RSocketService)
internal fun failRequest(statusCode: RSocketFailureCode, message: String): Nothing {
    throw RSocketError.Custom(statusCode.code, message)
}