package io.timemates.api.rsocket.timers

import io.timemates.api.rsocket.internal.createOrFail
import io.timemates.api.timers.sessions.types.TimerState
import io.timemates.api.timers.types.Timer
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.timers.fsm.*
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.rsproto.server.RSocketService
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import io.timemates.api.timers.members.invites.types.Invite as RSInvite
import io.timemates.backend.timers.fsm.TimerState as CoreTimerState
import io.timemates.backend.timers.types.Timer as CoreTimer

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
    return Timer.Settings.create {
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
    return Timer.create {
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
        is ConfirmationState -> TimerState.PhaseOneOf.ConfirmationWaiting(
            TimerState.ConfirmationWaiting.create {
                endsAt = endsTime
            }
        )

        is InactiveState -> TimerState.PhaseOneOf.Inactive(TimerState.Inactive.Default)
        is PauseState -> TimerState.PhaseOneOf.Paused(TimerState.Paused.Default)
        is RestState -> TimerState.PhaseOneOf.Rest(
            TimerState.Rest.create {
                endsAt = endsTime
            }
        )

        is RunningState -> TimerState.PhaseOneOf.Running(
            TimerState.Running.create {
                endsAt = endsTime
            }
        )
    }

    return TimerState.create {
        this.phase = phase
        publishTime = this@rs.publishTime.inMilliseconds
    }
}

internal fun Invite.rs(): RSInvite = RSInvite.create {
    code = this@rs.code.string
    limit = this@rs.limit.int
    creationTime = this@rs.creationTime.inMilliseconds
}