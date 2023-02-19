package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.TimerInvitesRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime

class InMemoryTimerInvitesRepository : TimerInvitesRepository {
    private val list = mutableListOf<TimerInvitesRepository.Invite>()

    override suspend fun getInvites(timerId: TimersRepository.TimerId): List<TimerInvitesRepository.Invite> {
        return list.filter { it.timerId == timerId }
    }

    override suspend fun removeInvite(code: TimerInvitesRepository.Code) {
        list.removeIf { it.code == code }
    }

    override suspend fun getInvite(code: TimerInvitesRepository.Code): TimerInvitesRepository.Invite? {
        return list.firstOrNull { it.code == code }
    }

    override suspend fun setInviteLimit(code: TimerInvitesRepository.Code, limit: Count) {
        val item = list.firstOrNull { it.code == code } ?: throw IllegalStateException("Invite not found")
        list.removeIf { it.code == code }
        list += TimerInvitesRepository.Invite(item.timerId, item.code, item.creationTime, limit)
    }

    override suspend fun getInvitesCount(timerId: TimersRepository.TimerId, after: UnixTime): Int {
        return list.filter { it.timerId == timerId && it.creationTime.long > after.long }
            .size
    }

    override suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        creationTime: UnixTime,
        limit: Count
    ) {
        list += TimerInvitesRepository.Invite(timerId, code, creationTime, limit)
    }
}