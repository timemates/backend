package io.timemates.backend.timers.data

import com.timemates.backend.time.UnixTime
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.pagination.map
import io.timemates.backend.timers.data.cache.CacheTimersDataSource
import io.timemates.backend.timers.data.db.TableTimerParticipantsDataSource
import io.timemates.backend.timers.data.db.TableTimersDataSource
import io.timemates.backend.timers.data.mappers.TimersMapper
import io.timemates.backend.timers.domain.repositories.TimersRepository
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.timers.TimerSettings
import io.timemates.backend.types.timers.value.InviteCode
import io.timemates.backend.types.timers.value.TimerDescription
import io.timemates.backend.types.timers.value.TimerId
import io.timemates.backend.types.timers.value.TimerName
import io.timemates.backend.types.users.value.UserId
import io.timemates.backend.validation.annotations.ValidationDelicateApi
import io.timemates.backend.validation.createUnsafe

@OptIn(ValidationDelicateApi::class)
class PostgresqlTimersRepository(
    private val tableTimers: TableTimersDataSource,
    private val cachedTimers: CacheTimersDataSource,
    private val tableTimerParticipants: TableTimerParticipantsDataSource,
    private val timersMapper: TimersMapper,
) : TimersRepository {
    override suspend fun createTimer(
        name: TimerName,
        description: TimerDescription,
        settings: TimerSettings,
        ownerId: UserId,
        creationTime: UnixTime,
    ): TimerId {
        val id = tableTimers.createTimer(
            name = name.string,
            description = null,
            ownerId = ownerId.long,
            creationTime = creationTime.inMilliseconds,
        )

        tableTimers.setSettings(
            id = id,
            settings = settings.let(timersMapper::domainSettingsToDbSettingsPatchable)
        )

        return TimerId.createUnsafe(id)
    }

    override suspend fun getTimerInformation(timerId: TimerId): TimersRepository.TimerInformation? {
        return tableTimers.getTimer(timerId.long)
            ?.let { dbTimer ->
                timersMapper.dbTimerToDomainTimerInformation(
                    dbTimer = dbTimer,
                    membersCount = tableTimerParticipants.getParticipantsCount(timerId.long, 0).toInt(),
                )
            }
    }

    override suspend fun removeTimer(timerId: TimerId) {
        tableTimers.removeTimer(timerId.long)
        cachedTimers.remove(timerId.long)
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

    override suspend fun addMember(userId: UserId, timerId: TimerId, joinTime: UnixTime, inviteCode: InviteCode?) {
        tableTimerParticipants.addParticipant(
            timerId = timerId.long,
            userId = userId.long,
            joinTime = joinTime.inMilliseconds,
            inviteCode = inviteCode?.string,
        )
    }

    override suspend fun removeMember(userId: UserId, timerId: TimerId) {
        tableTimerParticipants.removeParticipant(timerId.long, userId.long)
    }

    override suspend fun getMembers(timerId: TimerId, pageToken: PageToken?): Page<UserId> {
        return tableTimerParticipants.getParticipants(
            timerId.long, pageToken,
        ).map { id -> UserId.createUnsafe(id) }
    }

    override suspend fun getMembersCountOfInvite(timerId: TimerId, inviteCode: InviteCode): Count {
        return tableTimerParticipants.getParticipantsCountOfInvite(timerId.long, inviteCode.string)
            .let { Count.createUnsafe(it) }
    }

    override suspend fun isMemberOf(userId: UserId, timerId: TimerId): Boolean {
        return tableTimerParticipants.isMember(timerId.long, userId.long)
    }

    override suspend fun getTimersInformation(userId: UserId, pageToken: PageToken?): Page<TimersRepository.TimerInformation> {
        return tableTimers.getTimers(userId.long, pageToken).map {
            timersMapper.dbTimerToDomainTimerInformation(
                dbTimer = it,
                membersCount = tableTimerParticipants.getParticipantsCount(it.id, 0).toInt(),
            )
        }
    }

    override suspend fun setTimerInformation(timerId: TimerId, information: TimersRepository.TimerInformation.Patch) {
        tableTimers.editTimer(
            id = timerId.long,
            newName = information.name?.string,
            newDescription = information.description?.string
        )

        cachedTimers.remove(timerId.long)
    }
}