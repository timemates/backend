@file:Suppress("MemberVisibilityCanBePrivate")

package io.timemates.backend.data.timers.mappers

import com.timemates.backend.time.TimeProvider
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.common.markers.Mapper
import io.timemates.backend.data.timers.db.entities.DbTimer
import io.timemates.backend.data.timers.db.tables.TimersStateTable
import io.timemates.backend.data.timers.db.tables.TimersTable
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.Timer
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.value.TimerDescription
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.validation.createOrThrowInternally
import org.jetbrains.exposed.sql.ResultRow
import kotlin.time.Duration.Companion.minutes

class TimersMapper(private val sessionMapper: TimerSessionMapper) : Mapper {
    fun resultRowToDbTimer(resultRow: ResultRow) = with(resultRow) {
        return@with DbTimer(
            get(TimersTable.ID),
            get(TimersTable.NAME),
            get(TimersTable.DESCRIPTION),
            get(TimersTable.OWNER_ID),
            resultRowToTimerSettings(resultRow),
            get(TimersTable.CREATION_TIME),
        )
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
            get(TimersStateTable.CREATION_TIME),
        )
    }

    fun dbTimerToDomainTimer(
        dbTimer: DbTimer,
        membersCount: Int,
        state: DbTimer.State,
        timeProvider: TimeProvider,
        timersRepository: TimersRepository,
        timerSessionRepository: TimerSessionRepository,
    ): Timer {
        return Timer(
            id = TimerId.createOrThrowInternally(dbTimer.id),
            name = TimerName.createOrThrowInternally(dbTimer.name),
            description = dbTimer.description.takeUnless { it.isBlank() }
                ?.let { TimerDescription.createOrThrowInternally(it) },
            ownerId = UserId.createOrThrowInternally(dbTimer.ownerId),
            settings = dbSettingsToDomainSettings(dbTimer.settings),
            membersCount = Count.createOrThrowInternally(membersCount),
            state = sessionMapper.dbStateToFsmState(
                dbState = state,
                timeProvider = timeProvider,
                timersRepository = timersRepository,
                timersSessionRepository = timerSessionRepository
            ),
        )
    }

    fun dbSettingsToDomainSettings(
        dbSettings: DbTimer.Settings,
    ): TimerSettings = with(dbSettings) {
        return TimerSettings(
            workTime = workTime.minutes,
            restTime = restTime.minutes,
            bigRestTime = bigRestTime.minutes,
            bigRestEnabled = bigRestEnabled,
            bigRestPer = Count.createOrThrowInternally(bigRestPer),
            isEveryoneCanPause = isEveryoneCanPause,
            isConfirmationRequired = isConfirmationRequired,
        )
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
            id = TimerId.createOrThrowInternally(id),
            name = TimerName.createOrThrowInternally(name),
            description = TimerDescription.createOrThrowInternally(description),
            ownerId = UserId.createOrThrowInternally(ownerId),
            settings = dbSettingsToDomainSettings(settings),
            membersCount = Count.createOrThrowInternally(membersCount),
        )
    }
}

private const val ENDS_AT_REQUIRED = "endsAt is required for given state, but it's null"