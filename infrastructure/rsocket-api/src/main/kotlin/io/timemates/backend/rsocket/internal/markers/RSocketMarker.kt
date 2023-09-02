package io.timemates.backend.rsocket.internal.markers

/**
 * Interface-marker for RSocket-based operations that should throw
 * type-safe failures.
 *
 * @see io.timemates.backend.rsocket.internal.failRequest
 * @see io.timemates.backend.rsocket.internal.createOrFail
 */
sealed interface RSocketMarker

interface RSocketService : RSocketMarker

interface RSocketMapper : RSocketMarker