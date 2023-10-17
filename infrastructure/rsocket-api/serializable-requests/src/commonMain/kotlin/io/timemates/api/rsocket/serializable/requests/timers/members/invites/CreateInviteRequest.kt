package io.timemates.api.rsocket.serializable.requests.timers.members.invites

import io.timemates.api.rsocket.serializable.requests.common.RSocketRequest
import kotlinx.serialization.Serializable

@Serializable
data class CreateInviteRequest(
    val timerId: Long,
    val maxJoiners: Int,
) : RSocketRequest<CreateInviteRequest.Result> {
    companion object Key : RSocketRequest.Key<CreateInviteRequest>

    override val key: RSocketRequest.Key<*>
        get() = Key
    @Serializable
    data class Result(val inviteCode: String)
}