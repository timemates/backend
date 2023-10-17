package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class RemoveInviteRequest(
    val timerId: Long,
    val code: String,
) : RSocketRequest<RemoveInviteRequest.Result> {
    companion object Key : RSocketRequest.Key<RemoveInviteRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key

    data object Result
}