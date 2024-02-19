package org.timemates.api.rsocket.timers

import io.timemates.rsproto.server.RSocketService
import org.timemates.api.rsocket.internal.createOrFail
import org.timemates.api.timers.sessions.types.TimerState
import org.timemates.api.timers.types.Timer
import org.timemates.backend.types.common.value.Count
import org.timemates.backend.types.timers.Invite
import org.timemates.backend.types.timers.TimerSettings
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import org.timemates.api.timers.members.invites.types.Invite as RSInvite
import org.timemates.backend.types.timers.Timer as CoreTimer
import org.timemates.backend.types.timers.TimerState as CoreTimerState

context(RSocketService)
internal fun Timer.Settings.core(): TimerSettings {
    return TimerSettings(
        workTime = workTime.milliseconds,
        restTime = restTime.milliseconds,
        bigRestTime = bigRestTime.milliseconds,
        bigRestEnabled = bigRestEnabled,
        bigRestPer = Count.createOrFail(bigRestPer),
        isEveryoneCanPause = isEveryoneCanPause,
        isConfirmationRequired = isConfirmationRequired
    )
}

internal fun TimerSettings.rs(): Timer.Settings {
    return Timer.Settings {
        workTime = this@rs.workTime.toInt(DurationUnit.SECONDS)
        restTime = this@rs.restTime.toInt(DurationUnit.SECONDS)
        bigRestTime = this@rs.bigRestTime.toInt(DurationUnit.SECONDS)
        bigRestPer = this@rs.bigRestPer.int
        bigRestEnabled = this@rs.bigRestEnabled
        isEveryoneCanPause = this@rs.isEveryoneCanPause
        isConfirmationRequired = this@rs.isConfirmationRequired
    }
}

internal fun CoreTimer.rs(): Timer {
    return Timer {
        id = this@rs.id.long
        name = this@rs.name.string
        description = this@rs.description?.string.orEmpty()
        ownerId = this@rs.ownerId.long
        settings = this@rs.settings.rs()
        membersCount = this@rs.membersCount.int
        currentState = this@rs.state.rs()
    }
}

internal fun CoreTimerState.rs(): TimerState {
    val endsTime = (publishTime + alive).inMilliseconds

    val phase = when (this) {
        is CoreTimerState.ConfirmationWaiting -> TimerState.PhaseOneOf.ConfirmationWaiting {
            endsAt = endsTime
        }

        is CoreTimerState.Inactive -> TimerState.PhaseOneOf.Inactive.Default
        is CoreTimerState.Paused -> TimerState.PhaseOneOf.Paused.Default
        is CoreTimerState.Rest -> TimerState.PhaseOneOf.Rest {
            endsAt = endsTime
        }

        is CoreTimerState.Running -> TimerState.PhaseOneOf.Running {
            endsAt = endsTime
        }
    }

    return TimerState {
        this.phase = phase
        publishTime = this@rs.publishTime.inMilliseconds
    }
}

internal fun Invite.rs(): RSInvite = RSInvite {
    code = this@rs.code.string
    limit = this@rs.limit.int
    creationTime = this@rs.creationTime.inMilliseconds
}