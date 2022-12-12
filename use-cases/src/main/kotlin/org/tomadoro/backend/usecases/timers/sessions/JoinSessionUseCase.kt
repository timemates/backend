package org.tomadoro.backend.usecases.timers.sessions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.tomadoro.backend.domain.value.toDuration
import org.tomadoro.backend.providers.CurrentTimeProvider
import org.tomadoro.backend.repositories.*

class JoinSessionUseCase(
    private val timers: TimersRepository,
    private val sessions: SessionsRepository,
    private val schedules: SchedulesRepository,
    private val time: CurrentTimeProvider,
    private val users: UsersRepository
) {
    suspend operator fun invoke(
        userId: UsersRepository.UserId,
        timerId: TimersRepository.TimerId
    ): Result {
        if (!timers.isMemberOf(userId, timerId))
            return Result.NotFound

        sessions.sendUpdate(
            timerId,
            SessionsRepository.Update.UserHasJoined(users.getUser(userId)!!)
        )
        sessions.addMember(timerId, userId)

        val updates = sessions.updatesOf(timerId)

        schedules.single(timerId) {
            var pomodoros = 0

            updates.collectLatest {
                when (it) {
                    is SessionsRepository.Update.TimerStarted -> {
                        val settings = timers.getTimerSettings(timerId)!!

                        cancelAndSchedule(timerId, settings.workTime.toDuration()) {
                            sessions.sendUpdate(
                                timerId,
                                SessionsRepository.Update.TimerStopped(
                                    startsAt = time.provide() +
                                        if (pomodoros == settings.bigRestPer && settings.bigRestEnabled)
                                            settings.bigRestTime.also { pomodoros = 0 }
                                        else settings.restTime
                                )
                            )
                            pomodoros++
                        }
                    }

                    is SessionsRepository.Update.TimerStopped -> {
                        if (it.startsAt == null)
                            return@collectLatest

                        cancelAndSchedule(
                            timerId,
                            (time.provide() - it.startsAt).toDuration()
                        ) {
                            val settings = timers.getTimerSettings(timerId)!!
                            if (settings.isConfirmationRequired) {
                                sessions.sendUpdate(
                                    timerId,
                                    SessionsRepository.Update.Confirmation
                                )
                            } else {
                                sessions.sendUpdate(
                                    timerId,
                                    SessionsRepository.Update.TimerStarted(
                                        time.provide() + settings.workTime
                                    )
                                )
                            }
                        }

                    }

                    is SessionsRepository.Update.SessionFinished -> {
                        cancel(timerId)
                        unbindSingle(timerId)
                    }

                    else -> {}
                }
            }
        }

        return Result.Success(updates)
    }

    sealed interface Result {
        @JvmInline
        value class Success(val updates: Flow<SessionsRepository.Update>) : Result

        object NotFound : Result
    }
}