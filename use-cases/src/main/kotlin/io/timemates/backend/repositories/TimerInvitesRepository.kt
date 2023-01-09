package io.timemates.backend.repositories

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime

interface TimerInvitesRepository {
    suspend fun getInvites(timerId: TimersRepository.TimerId): List<Invite>
    suspend fun removeInvite(code: Code)
    suspend fun getInvite(code: Code): Invite?
    suspend fun setInviteLimit(code: Code, limit: Count)

    suspend fun getInvitesCount(timerId: TimersRepository.TimerId, after: UnixTime): Int

    suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: Code,
        creationTime: UnixTime,
        limit: Count
    )

    class Invite(
        val timerId: TimersRepository.TimerId,
        val code: Code,
        val creationTime: UnixTime,
        val limit: Count
    )

    @JvmInline
    value class Code(val string: String) {
        companion object {
            const val SIZE = 8
        }
    }
}