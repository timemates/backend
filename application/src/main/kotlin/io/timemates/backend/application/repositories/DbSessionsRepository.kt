package io.timemates.backend.application.repositories

import io.timemates.backend.integrations.postgresql.repositories.datasource.DbSessionsDataSource
import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.DetailedTimer
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime

class DbSessionsRepository(
    private val dbSessionsDataSource: DbSessionsDataSource
) : SessionsRepository {
    override suspend fun addMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId,
        dateTime: UnixTime
    ) {
        dbSessionsDataSource.addUserToSession(timerId.int, userId.int, dateTime.long)
    }

    override suspend fun removeMember(timerId: TimersRepository.TimerId, userId: UsersRepository.UserId) {
        dbSessionsDataSource.removeFromSession(timerId.int, userId.int)
    }

    override suspend fun getMembers(
        timerId: TimersRepository.TimerId,
        afterUserId: UsersRepository.UserId?,
        count: Count
    ): List<UsersRepository.UserId> {
        return dbSessionsDataSource.getUsersInSession(
            timerId.int,
            afterUserId?.int,
            count.int
        ).map { UsersRepository.UserId(it) }
    }

    override suspend fun getActive(ids: List<TimersRepository.TimerId>): Map<TimersRepository.TimerId, DetailedTimer.Active.SessionInfo> {
        TODO()
    }

    override suspend fun createConfirmation(timerId: TimersRepository.TimerId) {
        dbSessionsDataSource.startConfirmationState(timerId.int)
    }

    override suspend fun isConfirmationAvailable(timerId: TimersRepository.TimerId): Boolean {
        return dbSessionsDataSource.isConfirmation(timerId.int)
    }

    override suspend fun confirm(timerId: TimersRepository.TimerId, userId: UsersRepository.UserId): Boolean {
        dbSessionsDataSource.confirmAttendance(timerId.int, userId.int)
        return dbSessionsDataSource.isEveryoneConfirmed(timerId.int)
    }
}