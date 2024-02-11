package io.timemates.backend.timers.data.mappers

import com.timemates.backend.time.UnixTime
import io.timemates.backend.timers.data.db.entities.DbSessionUser
import io.timemates.backend.timers.data.db.entities.DbTimer
import io.timemates.backend.timers.data.db.tables.TimersSessionUsersTable
import io.timemates.backend.timers.domain.types.TimerState
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe
import org.jetbrains.exposed.sql.ResultRow
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ValidationDelicateApi::class)
class TimerSessionMapper {
    fun resultRowToSessionUser(resultRow: ResultRow): DbSessionUser = with(resultRow) {
        return DbSessionUser(
            timerId = get(TimersSessionUsersTable.TIMER_ID),
            userId = get(TimersSessionUsersTable.USER_ID),
            lastActivityTime = get(TimersSessionUsersTable.LAST_ACTIVITY_TIME),
        )
    }

    fun dbStateToFsmState(
        dbState: DbTimer.State,
    ): TimerState = with(dbState) {
        val publishTime = UnixTime.createUnsafe(creationTime)
        val alive = (endsAt?.let { (creationTime - it) } ?: Long.MAX_VALUE).milliseconds

        val state = when (phase) {
            DbTimer.State.Phase.ATTENDANCE_CONFIRMATION -> TimerState.ConfirmationWaiting(
                publishTime = publishTime,
                alive = alive,
            )

            DbTimer.State.Phase.OFFLINE -> TimerState.Inactive(
                publishTime = publishTime,
            )

            DbTimer.State.Phase.PAUSED ->
                TimerState.Paused(
                    publishTime = UnixTime.createUnsafe(creationTime),
                )

            DbTimer.State.Phase.REST ->
                TimerState.Rest(
                    alive = alive,
                    publishTime = publishTime,
                )

            DbTimer.State.Phase.RUNNING ->
                TimerState.Running(
                    alive = alive,
                    publishTime = publishTime,
                )
        }

        return@with state
    }


    fun fsmStateToDbState(state: TimerState): DbTimer.State = with(state) {
        val phase = when (state) {
            is TimerState.ConfirmationWaiting -> DbTimer.State.Phase.ATTENDANCE_CONFIRMATION
            is TimerState.Inactive -> DbTimer.State.Phase.OFFLINE
            is TimerState.Paused -> DbTimer.State.Phase.PAUSED
            is TimerState.Rest -> DbTimer.State.Phase.REST
            is TimerState.Running -> DbTimer.State.Phase.RUNNING
        }

        return@with DbTimer.State(
            phase = phase,
            endsAt = (publishTime + alive).inMilliseconds,
            creationTime = publishTime.inMilliseconds
        )
    }
}