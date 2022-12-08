package org.tomadoro.backend.repositories.integration

import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.domain.Milliseconds
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.repositories.integration.datasource.TimersDatabaseDataSource
import org.tomadoro.backend.repositories.TimersRepository as TimersRepositoryContract

class TimersRepository(
    private val datasource: TimersDatabaseDataSource
) : TimersRepositoryContract {
    override suspend fun createTimer(
        name: TimerName,
        settings: TimersRepository.Settings,
        ownerId: UsersRepository.UserId,
        creationTime: DateTime
    ): TimersRepository.TimerId {
        return TimersRepositoryContract.TimerId(
            datasource.createTimer(
                name.string,
                creationTime.long,
                ownerId.int,
                settings.toInternalSettings()
            )
        )
    }

    override suspend fun getTimer(timerId: TimersRepository.TimerId): TimersRepository.Timer? {
        return datasource.getTimerById(timerId.int)?.toExternalTimer()
    }

    override suspend fun removeTimer(timerId: TimersRepository.TimerId) {
        datasource.removeTimer(timerId.int)
    }

    override suspend fun getTimerSettings(timerId: TimersRepository.TimerId): TimersRepository.Settings? {
        return datasource.getSettings(timerId.int)?.toExternalSettings()
    }

    override suspend fun setTimerSettings(timerId: TimersRepository.TimerId, settings: TimersRepository.NewSettings) {
        datasource.setNewSettings(timerId.int, settings.toInternalPatchable())
    }

    override suspend fun addMember(userId: UsersRepository.UserId, timerId: TimersRepository.TimerId) {
        datasource.addMember(timerId.int, userId.int)
    }

    override suspend fun getMembers(
        timerId: TimersRepository.TimerId,
        fromUser: UsersRepository.UserId?,
        count: Count
    ): Sequence<UsersRepository.UserId> {
        return datasource.getMembersIds(timerId.int, fromUser?.int ?: 0, count.int)
            .map { UsersRepository.UserId(it) }
    }

    override suspend fun isMemberOf(userId: UsersRepository.UserId, timerId: TimersRepository.TimerId): Boolean {
        return datasource.isMemberOf(timerId.int, userId.int)
    }

    override suspend fun getTimers(
        userId: UsersRepository.UserId,
        fromTimer: TimersRepository.TimerId?,
        count: Count
    ): Sequence<TimersRepository.Timer> {
        return datasource.getUserTimers(userId.int, fromTimer?.int ?: 0, count.int).map { it.toExternalTimer() }
    }

    private fun TimersDatabaseDataSource.Timer.toExternalTimer(): TimersRepositoryContract.Timer {
        return TimersRepository.Timer(
            TimersRepository.TimerId(id),
            TimerName(timerName),
            UsersRepository.UserId(ownerId),
            settings.toExternalSettings()
        )
    }

    private fun TimersDatabaseDataSource.Timer.Settings.toExternalSettings(): TimersRepositoryContract.Settings {
        return TimersRepositoryContract.Settings(
            Milliseconds(workTime), Milliseconds(restTime), Milliseconds(bigRestTime), bigRestEnabled,
            bigRestPer, isEveryoneCanPause, isConfirmationRequired
        )
    }

    private fun TimersRepositoryContract.NewSettings.toInternalPatchable(): TimersDatabaseDataSource.Timer.Settings.Patchable {
        return TimersDatabaseDataSource.Timer.Settings.Patchable(
            workTime?.long,
            restTime?.long,
            bigRestTime?.long,
            bigRestEnabled,
            bigRestPer,
            isEveryoneCanPause,
            isConfirmationRequired
        )
    }

    private fun TimersRepositoryContract.Settings.toInternalSettings(): TimersDatabaseDataSource.Timer.Settings {
        return TimersDatabaseDataSource.Timer.Settings(
            workTime.long,
            restTime.long,
            bigRestTime.long,
            bigRestEnabled,
            bigRestPer,
            isEveryoneCanPause,
            isConfirmationRequired
        )
    }
}