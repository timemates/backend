package io.timemates.backend.data.timers.mappers

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import io.timemates.backend.validation.createOrThrowInternally
import io.timemates.backend.data.common.markers.Mapper
import io.timemates.backend.data.timers.db.entities.DbSessionUser
import io.timemates.backend.data.timers.db.entities.DbTimer
import io.timemates.backend.data.timers.db.tables.TimersSessionUsersTable
import io.timemates.backend.timers.fsm.*
import io.timemates.backend.timers.repositories.TimerSessionRepository
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.value.TimerId
import org.jetbrains.exposed.sql.ResultRow
import kotlin.time.Duration.Companion.milliseconds

class TimerSessionMapper : Mapper {
    fun resultRowToSessionUser(resultRow: ResultRow): DbSessionUser = with(resultRow) {
        return DbSessionUser(
            timerId = get(TimersSessionUsersTable.TIMER_ID),
            userId = get(TimersSessionUsersTable.USER_ID),
            lastActivityTime = get(TimersSessionUsersTable.LAST_ACTIVITY_TIME),
        )
    }

    fun dbStateToFsmState(
        dbState: DbTimer.State,
        timeProvider: TimeProvider,
        timersRepository: TimersRepository,
        timersSessionRepository: TimerSessionRepository,
    ): TimerState = with(dbState) {
        val timerId = TimerId.createOrThrowInternally(timerId)
        val publishTime = UnixTime.createOrThrowInternally(creationTime)
        val alive = (endsAt?.let { (creationTime - it) } ?: Long.MAX_VALUE).milliseconds

        val state = when (phase) {
            DbTimer.State.Phase.ATTENDANCE_CONFIRMATION -> ConfirmationState(
                timerId = timerId,
                timeProvider = timeProvider,
                timersRepository = timersRepository,
                publishTime = publishTime,
                alive = alive,
                timerSessionRepository = timersSessionRepository,
            )

            DbTimer.State.Phase.OFFLINE -> InactiveState(
                timerId = timerId,
                publishTime = publishTime,
                timerSessionRepository = timersSessionRepository,
                timeProvider = timeProvider,
                timersRepository = timersRepository,
            )

            DbTimer.State.Phase.PAUSED ->
                PauseState(
                    timerId = timerId,
                    publishTime = UnixTime.createOrThrowInternally(creationTime),
                    timersRepository = timersRepository,
                    timeProvider = timeProvider,
                    timerSessionRepository = timersSessionRepository,
                )

            DbTimer.State.Phase.REST ->
                RestState(
                    timerId = timerId,
                    timersRepository = timersRepository,
                    timeProvider = timeProvider,
                    timerSessionRepository = timersSessionRepository,
                    alive = alive,
                    publishTime = publishTime,
                )

            DbTimer.State.Phase.RUNNING ->
                RunningState(
                    timerId = timerId,
                    timersRepository = timersRepository,
                    timeProvider = timeProvider,
                    timerSessionRepository = timersSessionRepository,
                    alive = alive,
                    publishTime = publishTime,
                )
        }

        return@with state
    }


    fun fsmStateToDbState(state: TimerState): DbTimer.State = with(state) {
        val phase = when (state) {
            is ConfirmationState -> DbTimer.State.Phase.ATTENDANCE_CONFIRMATION
            is InactiveState -> DbTimer.State.Phase.OFFLINE
            is PauseState -> DbTimer.State.Phase.PAUSED
            is RestState -> DbTimer.State.Phase.REST
            is RunningState -> DbTimer.State.Phase.RUNNING
        }

        return@with DbTimer.State(
            timerId = timerId.long,
            phase = phase,
            endsAt = (publishTime + alive).inMilliseconds,
            creationTime = publishTime.inMilliseconds
        )
    }
}