package io.timemates.backend.repositories

import io.timemates.backend.types.value.Count

interface TimerInvitesRepository {
    suspend fun getInvites(timerId: TimersRepository.TimerId): List<Invite>
    suspend fun removeInvite(code: Code)
    suspend fun getInvite(code: Code): Invite?
    suspend fun setInviteLimit(code: Code, limit: Count)

    suspend fun createInvite(
        timerId: TimersRepository.TimerId,
        code: Code,
        limit: Count
    )

    class Invite(
        val timerId: TimersRepository.TimerId,
        val code: Code,
        val limit: Count
    )

    @JvmInline
    value class Code(val string: String) {
        companion object {
            const val SIZE = 8
        }
    }
}