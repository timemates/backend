package org.timemates.backend.types.timers

import com.timemates.backend.time.UnixTime
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.timers.value.InviteCode
import org.timemates.backend.types.timers.value.TimerId

data class Invite(
    val timerId: TimerId,
    val code: InviteCode,
    val creationTime: UnixTime,
    val limit: Count,
)