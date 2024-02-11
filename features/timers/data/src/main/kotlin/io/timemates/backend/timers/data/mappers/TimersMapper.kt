@file:Suppress("MemberVisibilityCanBePrivate")

package io.timemates.backend.timers.data.mappers

import io.timemates.backend.timers.data.db.entities.DbTimer
import io.timemates.backend.timers.data.db.tables.TimersStateTable
import io.timemates.backend.timers.data.db.tables.TimersTable
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.Timer
import io.timemates.backend.types.timers.TimerSettings
import io.timemates.backend.types.timers.value.TimerDescription
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.timers.value.TimerName
import io.timemates.backend.types.users.value.UserId
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
import org.jetbrains.exposed.sql.ResultRow
import kotlin.time.Duration.Companion.minutes

@OptIn(ValidationDelicateApi::class)
class TimersMapper(private val sessionMapper: TimerSessionMapper) {
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
            get(TimersStateTable.PHASE),
            get(TimersStateTable.ENDS_AT),
            get(TimersStateTable.CREATION_TIME),
        )
    }

    fun dbTimerToDomainTimer(
        dbTimer: DbTimer,
        membersCount: Int,
        state: DbTimer.State,
    ): Timer {
        return Timer(
            id = TimerId.createUnsafe(dbTimer.id),
            name = TimerName.createUnsafe(dbTimer.name),
            description = dbTimer.description.takeUnless { it.isBlank() }
                ?.let { TimerDescription.createUnsafe(it) },
            ownerId = UserId.createUnsafe(dbTimer.ownerId),
            settings = dbSettingsToDomainSettings(dbTimer.settings),
            membersCount = Count.createUnsafe(membersCount),
            state = sessionMapper.dbStateToFsmState(
                dbState = state,
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
            bigRestPer = Count.createUnsafe(bigRestPer),
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
            id = TimerId.createUnsafe(id),
            name = TimerName.createUnsafe(name),
            description = TimerDescription.createUnsafe(description),
            ownerId = UserId.createUnsafe(ownerId),
            settings = dbSettingsToDomainSettings(settings),
            membersCount = Count.createUnsafe(membersCount),
        )
    }
}

private const val ENDS_AT_REQUIRED = "endsAt is required for given state, but it's null"