package io.timemates.api.rsocket.serializable.requests.timers.members

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest

data class KickMemberRequest(
    val timerId: Long,
    val userId: Long,
) : RSocketRequest<KickMemberRequest.Result> {
    companion object Key : RSocketRequest.Key<KickMemberRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}