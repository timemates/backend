@file:Suppress("MemberVisibilityCanBePrivate")

package io.timemates.backend.data.timers.datasource.db.mappers

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.datasource.cache.entities.CachedTimer
import io.timemates.backend.data.timers.datasource.db.entities.DbTimer
import io.timemates.backend.data.timers.datasource.db.tables.TimersStateTable
import io.timemates.backend.data.timers.datasource.db.tables.TimersTable
import io.timemates.backend.data.timers.datasource.realtime.entities.RealtimeState
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.TimerState
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.users.types.value.UserId
import org.jetbrains.exposed.sql.ResultRow
import kotlin.time.Duration.Companion.minutes

class TimersMapper {
    fun resultRowToDbTimer(resultRow: ResultRow) = with(resultRow) {
        return@with DbTimer(
            get(TimersTable.ID),
            get(TimersTable.NAME),
            get(TimersTable.DESCRIPTION),
            get(TimersTable.OWNER_ID),
            resultRowToTimerSettings(resultRow),
        )
    }

    fun realtimeStateToDomainState(realtimeState: RealtimeState): TimerState = with(realtimeState) {
        return@with when (phase) {
            RealtimeState.Phase.RUNNING ->
                TimerState.Active.Running(
                    UnixTime.createOrThrow(endsAt ?: error(ENDS_AT_REQUIRED))
                )

            RealtimeState.Phase.PAUSED -> TimerState.Active.Paused
            RealtimeState.Phase.ATTENDANCE_CONFIRMATION ->
                TimerState.Active.ConfirmationWaiting(
                    UnixTime.createOrThrow(endsAt ?: error(ENDS_AT_REQUIRED))
                )

            RealtimeState.Phase.REST -> TimerState.Active.Rest(
                UnixTime.createOrThrow(endsAt ?: error(ENDS_AT_REQUIRED))
            )

            RealtimeState.Phase.OFFLINE -> TimerState.Inactive
        }
    }

    fun resultRowToTimerSettings(resultRow: ResultRow) = with(resultRow) {
        return@with DbTimer.Settings(
            workTime = get(TimersTable.WORK_TIME),
            restTime = get(TimersTable.REST_TIME),
            bigRestTime = get(TimersTable.BIG_REST_TIME),
            bigRestEnabled = get(TimersTable.BIG_REST_TIME_ENABLED),
            bigRestPer = get(TimersTable.BIG_REST_PER),
            isEveryoneCanPause = get(TimersTable.IS_EVERYONE_CAN_PAUSE),
            isConfirmationRequired = get(TimersTable.IS_CONFIRMATION_REQUIRED),
        )
    }

    fun resultRowToTimerState(resultRow: ResultRow) = with(resultRow) {
        return@with DbTimer.State(
            get(TimersStateTable.TIMER_ID),
            get(TimersStateTable.PHASE),
            get(TimersStateTable.ENDS_AT),
        )
    }

    fun domainStateToCacheState(timerId: Long, state: TimerState): CachedTimer.State = with(state) {
        val (phase, endsAt) = when(state) {
            is TimerState.Active.ConfirmationWaiting ->
                CachedTimer.State.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            TimerState.Active.Paused ->
                CachedTimer.State.Phase.ATTENDANCE_CONFIRMATION to null
            is TimerState.Active.Rest ->
                CachedTimer.State.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            is TimerState.Active.Running ->
                CachedTimer.State.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            TimerState.Inactive ->
                CachedTimer.State.Phase.ATTENDANCE_CONFIRMATION to null
        }

        return@with CachedTimer.State(timerId, phase, endsAt)
    }

    fun domainStateToDbState(timerId: Long, state: TimerState): DbTimer.State = with(state) {
        val (phase, endsAt) = when(state) {
            is TimerState.Active.ConfirmationWaiting ->
                DbTimer.State.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            TimerState.Active.Paused ->
                DbTimer.State.Phase.ATTENDANCE_CONFIRMATION to null
            is TimerState.Active.Rest ->
                DbTimer.State.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            is TimerState.Active.Running ->
                DbTimer.State.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            TimerState.Inactive ->
                DbTimer.State.Phase.ATTENDANCE_CONFIRMATION to null
        }

        return@with DbTimer.State(timerId, phase, endsAt)
    }

    fun dbTimerToDomainTimer(
        dbTimer: DbTimer,
        membersCount: Int,
        state: DbTimer.State,
    ): Timer {
        return Timer(
            id = TimerId.createOrThrow(dbTimer.id),
            name = TimerName.createOrThrow(dbTimer.name),
            description = TimerDescription.createOrThrow(dbTimer.description),
            ownerId = UserId.createOrThrow(dbTimer.ownerId),
            settings = dbSettingsToDomainSettings(dbTimer.settings),
            membersCount = Count.createOrThrow(membersCount),
            state = dbStateToDomainState(state),
        )
    }

    fun dbSettingsToDomainSettings(
        dbSettings: DbTimer.Settings): TimerSettings = with(dbSettings) {
        return TimerSettings(
            workTime = workTime.minutes,
            restTime = restTime.minutes,
            bigRestTime = bigRestTime.minutes,
            bigRestEnabled = bigRestEnabled,
            bigRestPer = Count.createOrThrow(bigRestPer),
            isEveryoneCanPause = isEveryoneCanPause,
        )
    }

    fun domainStateToRealtimeState(timerId: Long, state: TimerState): RealtimeState = with(state) {
        val (phase, endsAt) = when(state) {
            is TimerState.Active.ConfirmationWaiting ->
                RealtimeState.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            TimerState.Active.Paused ->
                RealtimeState.Phase.ATTENDANCE_CONFIRMATION to null
            is TimerState.Active.Rest ->
                RealtimeState.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            is TimerState.Active.Running ->
                RealtimeState.Phase.ATTENDANCE_CONFIRMATION to state.endsAt.inMilliseconds
            TimerState.Inactive ->
                RealtimeState.Phase.ATTENDANCE_CONFIRMATION to null
        }

        return RealtimeState(
            timerId = timerId,
            phase = phase,
            endsAt = endsAt,
            publishTime = state.timestamp.inMilliseconds,
        )
    }

    fun dbStateToDomainState(dbState: DbTimer.State): TimerState {
        return when (dbState.phase) {
            DbTimer.State.Phase.RUNNING ->
                TimerState.Active.Running(
                    UnixTime.createOrThrow(dbState.endsAt ?: error(ENDS_AT_REQUIRED))
                )

            DbTimer.State.Phase.PAUSED -> TimerState.Active.Paused
            DbTimer.State.Phase.ATTENDANCE_CONFIRMATION ->
                TimerState.Active.ConfirmationWaiting(
                    UnixTime.createOrThrow(dbState.endsAt ?: error(ENDS_AT_REQUIRED))
                )

            DbTimer.State.Phase.REST -> TimerState.Active.Rest(
                UnixTime.createOrThrow(dbState.endsAt ?: error(ENDS_AT_REQUIRED))
            )

            DbTimer.State.Phase.OFFLINE -> TimerState.Inactive
        }
    }

    fun cacheStateToDomainState(dbState: CachedTimer.State): TimerState {
        return when (dbState.phase) {
            CachedTimer.State.Phase.RUNNING ->
                TimerState.Active.Running(
                    UnixTime.createOrThrow(dbState.endsAt ?: error(ENDS_AT_REQUIRED))
                )

            CachedTimer.State.Phase.PAUSED -> TimerState.Active.Paused
            CachedTimer.State.Phase.ATTENDANCE_CONFIRMATION ->
                TimerState.Active.ConfirmationWaiting(
                    UnixTime.createOrThrow(dbState.endsAt ?: error(ENDS_AT_REQUIRED))
                )

            CachedTimer.State.Phase.REST -> TimerState.Active.Rest(
                UnixTime.createOrThrow(dbState.endsAt ?: error(ENDS_AT_REQUIRED))
            )

            CachedTimer.State.Phase.OFFLINE -> TimerState.Inactive
        }
    }

    fun domainSettingsToDbSettingsPatchable(patch: TimerSettings) = with(patch) {
        return@with DbTimer.Settings.Patchable(
            workTime = workTime.inWholeMilliseconds,
            restTime = restTime.inWholeMilliseconds,
            bigRestTime = bigRestTime.inWholeMilliseconds,
            bigRestEnabled = bigRestEnabled,
            bigRestPer = bigRestPer.int,
            isEveryoneCanPause = isEveryoneCanPause,
            isConfirmationRequired = isConfirmationRequired,
        )
    }

    fun domainPatchToDbPatchable(patch: TimerSettings.Patch) = with(patch) {
        return@with DbTimer.Settings.Patchable(
            workTime = workTime?.inWholeMilliseconds,
            restTime = restTime?.inWholeMilliseconds,
            bigRestTime = bigRestTime?.inWholeMilliseconds,
            bigRestEnabled = bigRestEnabled,
            bigRestPer = bigRestPer?.int,
            isEveryoneCanPause = isEveryoneCanPause,
            isConfirmationRequired = isConfirmationRequired,
        )
    }

    fun dbTimerToDomainTimerInformation(
        dbTimer: DbTimer,
        membersCount: Int,
    ): TimersRepository.TimerInformation = with(dbTimer) {
        return@with TimersRepository.TimerInformation(
            id = TimerId.createOrThrow(id),
            name = TimerName.createOrThrow(name),
            description = TimerDescription.createOrThrow(description),
            ownerId = UserId.createOrThrow(ownerId),
            settings = dbSettingsToDomainSettings(settings),
            membersCount = Count.createOrThrow(membersCount),
        )
    }
}

private const val ENDS_AT_REQUIRED = "endsAt is required for given state, but it's null"