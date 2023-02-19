package io.timemates.backend.timers.types

import com.timemates.backend.time.UnixTime
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId

data class Invite(
    val timerId: TimerId,
    val code: InviteCode,
    val creationTime: UnixTime,
    val limit: Count,
)