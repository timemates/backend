package io.timemates.backend.rsocket.timers.types.requests

data class GetUserTimersRequest(
    val pageToken: String? = null,
)