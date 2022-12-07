package org.tomadoro.backend.repositories

class MockedTimerInvitesRepository : TimerInvitesRepository {
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

    override suspend fun setInviteLimit(code: TimerInvitesRepository.Code, limit: TimerInvitesRepository.Count) {
        val item = list.firstOrNull { it.code == code } ?: throw IllegalStateException("Invite not found")
        list.removeIf { it.code == code }
        list += TimerInvitesRepository.Invite(item.timerId, item.code, limit)
    }

    override suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: TimerInvitesRepository.Code,
        limit: TimerInvitesRepository.Count
    ) {
        list += TimerInvitesRepository.Invite(timerId, code, limit)
    }
}