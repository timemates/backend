package io.timemates.backend.data.timers

import com.timemates.backend.time.UnixTime
import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.common.types.value.Count
import io.timemates.backend.data.timers.datasource.cache.CacheTimersStateDataSource
import io.timemates.backend.data.timers.datasource.db.TableTimerInvitesDataSource
import io.timemates.backend.data.timers.datasource.db.TableTimerParticipantsDataSource
import io.timemates.backend.data.timers.datasource.db.TableTimersDataSource
import io.timemates.backend.data.timers.datasource.db.TableTimersStateDataSource
import io.timemates.backend.data.timers.datasource.db.mappers.TimerInvitesMapper
import io.timemates.backend.data.timers.datasource.db.mappers.TimersMapper
import io.timemates.backend.timers.repositories.TimersRepository
import io.timemates.backend.timers.types.TimerSettings
import io.timemates.backend.timers.types.value.InviteCode
import io.timemates.backend.timers.types.value.TimerId
import io.timemates.backend.timers.types.value.TimerName
import io.timemates.backend.users.types.value.UserId

class PostgresqlTimersRepository(
    private val tableTimers: TableTimersDataSource,
    private val cachedTimers: TableTimersDataSource,
    private val tableTimersState: TableTimersStateDataSource,
    private val cachedTimersState: CacheTimersStateDataSource,
    private val tableTimerParticipants: TableTimerParticipantsDataSource,
    private val tableTimerInvites: TableTimerInvitesDataSource,
    private val timersMapper: TimersMapper,
    private val timerInvitesMapper: TimerInvitesMapper,
) : TimersRepository {
    override suspend fun createTimer(
        name: TimerName,
        settings: TimerSettings,
        ownerId: UserId,
        creationTime: UnixTime,
    ): TimerId {
        val id = tableTimers.createTimer(
            name = name.string,
            description = null,
            ownerId = ownerId.long,
        )

        tableTimers.setSettings(
            id = id,
            settings = settings.let(timersMapper::domainSettingsToDbSettingsPatchable)
        )

        return TimerId.createOrThrow(id)
    }

    override suspend fun getTimerInformation(timerId: TimerId): TimersRepository.TimerInformation? {
        return tableTimers.getTimer(timerId.long)
            ?.let { dbTimer ->
                timersMapper.dbTimerToDomainTimerInformation(
                    dbTimer = dbTimer,
                    membersCount = tableTimerParticipants.getParticipantsCount(timerId.long).toInt(),
                )
            }
    }

    override suspend fun removeTimer(timerId: TimerId) {
        tableTimers.removeTimer(timerId.long)
        cachedTimers.removeTimer(timerId.long)
    }

    override suspend fun getOwnedTimersCount(ownerId: UserId, after: UnixTime): Int {
        return tableTimers.getTimersCountOf(ownerId.long, after.inMilliseconds).toInt()
    }

    override suspend fun getTimerSettings(timerId: TimerId): TimerSettings? {
        return tableTimers.getTimer(timerId.long)
            ?.settings
            ?.let(timersMapper::dbSettingsToDomainSettings)
    }

    override suspend fun setTimerSettings(timerId: TimerId, settings: TimerSettings.Patch) {
        tableTimers.setSettings(
            timerId.long, settings.let(timersMapper::domainPatchToDbPatchable)
        )
    }

    override suspend fun addMember(userId: UserId, timerId: TimerId, joinTime: UnixTime, inviteCode: InviteCode) {
        tableTimerParticipants.addParticipant(
            timerId = timerId.long,
            userId = userId.long,
            joinTime = joinTime.inMilliseconds,
            inviteCode = inviteCode.string,
        )
    }

    override suspend fun removeMember(userId: UserId, timerId: TimerId) {
        tableTimerParticipants.removeParticipant(timerId.long, userId.long)
    }

    override suspend fun getMembers(timerId: TimerId, fromUser: UserId?, count: Count): List<UserId> {
        return tableTimerParticipants.getParticipants(
            timerId.long, count.int, fromUser?.long
        ).map { id -> UserId.createOrThrow(id) }
    }

    override suspend fun isMemberOf(userId: UserId, timerId: TimerId): Boolean {
        return tableTimerParticipants.isMember(timerId.long, userId.long)
    }

    override suspend fun getTimersInformation(userId: UserId, fromTimer: TimerId?, count: Count): List<TimersRepository.TimerInformation> {
        return tableTimers.getTimers(userId.long, fromTimer?.long).map {
            timersMapper.dbTimerToDomainTimerInformation(
                dbTimer = it,
                membersCount = tableTimerParticipants.getParticipantsCount(it.id).toInt(),
            )
        }
    }
}