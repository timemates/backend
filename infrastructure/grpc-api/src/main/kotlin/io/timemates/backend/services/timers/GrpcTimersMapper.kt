package io.timemates.backend.services.timers

import io.timemates.api.timers.members.invites.types.InviteOuterClass
import io.timemates.api.timers.requests.EditTimerInfoRequest.EditTimerRequest
import io.timemates.api.timers.sessions.types.TimerStateKt
import io.timemates.api.timers.sessions.types.TimerStateOuterClass
import io.timemates.api.timers.sessions.types.timerState
import io.timemates.api.timers.types.TimerOuterClass
import io.timemates.api.timers.types.timer
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.services.common.markers.GrpcMapper
import io.timemates.backend.services.common.validation.createOrStatus
import io.timemates.backend.timers.fsm.*
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Invite
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerName
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GrpcTimersMapper : GrpcMapper {
    fun toDomainSettings(settings: TimerOuterClass.Timer.Settings): TimerSettings {
        return TimerSettings(
            workTime = settings.workTime.seconds,
            restTime = settings.restTime.seconds,
            bigRestPer = Count.createOrStatus(settings.bigRestPer),
            bigRestEnabled = settings.bigRestEnabled,
            bigRestTime = settings.bigRestTime.seconds,
            isEveryoneCanPause = settings.isEveryoneCanPause,
            isConfirmationRequired = settings.isConfirmationRequired,
        )
    }

    fun toGrpcTimerSettings(settings: TimerSettings): TimerOuterClass.Timer.Settings {
        return TimerOuterClass.Timer.Settings.newBuilder()
            .setWorkTime(settings.workTime.inWholeMilliseconds.toInt())
            .setRestTime(settings.restTime.inWholeMilliseconds.toInt())
            .setBigRestEnabled(settings.bigRestEnabled)
            .setBigRestPer(settings.bigRestPer.int)
            .setBigRestTime(settings.bigRestTime.inWholeMilliseconds.toInt())
            .setIsConfirmationRequired(settings.isConfirmationRequired)
            .setIsEveryoneCanPause(settings.isEveryoneCanPause)
            .build()
    }

    fun toGrpcState(state: TimerState): TimerStateOuterClass.TimerState = timerState {
        publishTime = state.publishTime.inMilliseconds

        when (state) {
            is ConfirmationState ->
                confirmationWaiting = TimerStateOuterClass.TimerState.ConfirmationWaiting.getDefaultInstance()

            is InactiveState ->
                inactive = TimerStateOuterClass.TimerState.Inactive.getDefaultInstance()

            is PauseState ->
                paused = TimerStateOuterClass.TimerState.Paused.getDefaultInstance()

            is RestState ->
                rest = TimerStateKt.rest {
                    endsAt = endsAt
                }

            is RunningState -> running = TimerStateKt.running {
                endsAt = endsAt
            }
        }
    }

    fun toGrpcTimer(timer: Timer): TimerOuterClass.Timer {
        return timer {
            id = timer.id.long
            name = timer.name.string
            description = timer.description.string
            membersCount = timer.membersCount.int
            ownerId = timer.ownerId.long
            currentState = toGrpcState(timer.state)
        }
    }

    fun toTimerSettingsPatch(
        settings: TimerOuterClass.Timer.Settings.Patch,
    ): TimerSettings.Patch {
        return TimerSettings.Patch(
            workTime = settings.workTime.takeIf { settings.hasWorkTime() }?.milliseconds,
            restTime = settings.restTime.takeIf { settings.hasBigRestTime() }?.milliseconds,
            bigRestPer = if (settings.hasBigRestPer()) Count.createOrStatus(settings.bigRestPer) else null,
            bigRestEnabled = settings.bigRestEnabled.takeIf { settings.hasBigRestEnabled() },
            bigRestTime = settings.bigRestTime.takeIf { settings.hasBigRestTime() }?.milliseconds,
            isEveryoneCanPause = settings.isEveryoneCanPause.takeIf { settings.hasIsEveryoneCanPause() },
            isConfirmationRequired = settings.isConfirmationRequired.takeIf { settings.hasIsConfirmationRequired() },
        )
    }

    fun toTimerInfoPatch(
        patch: EditTimerRequest,
    ): TimersRepository.TimerInformation.Patch {
        return TimersRepository.TimerInformation.Patch(
            name = patch.name.takeIf { patch.hasName() }?.let { TimerName.createOrStatus(it) },
            description = patch.description.takeIf { patch.hasDescription() }
                ?.let { TimerDescription.createOrStatus(it) },
        )
    }

    fun toGrpcInvite(invite: Invite): InviteOuterClass.Invite {
        return InviteOuterClass.Invite.newBuilder()
            .setCode(invite.code.string)
            .setCreationTime(invite.creationTime.inMilliseconds)
            .setLimit(invite.limit.int)
            .build()
    }
}