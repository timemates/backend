package org.tomadoro.backend.repositories

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

    /**
     * Invite code.
     */
    @JvmInline
    value class Code(val string: String)

    /**
     * Specifies max count of users that can be joined by specific [Code].
     */
    @JvmInline
    value class Count(val int: Int)
}