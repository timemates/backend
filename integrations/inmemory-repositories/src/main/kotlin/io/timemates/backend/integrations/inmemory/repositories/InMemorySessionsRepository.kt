package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.SessionsRepository
import io.timemates.backend.repositories.TimersRepository
import io.timemates.backend.repositories.UsersRepository
import io.timemates.backend.types.DetailedTimer
import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.UnixTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

class InMemorySessionsRepository : SessionsRepository {
    private val sessions = ConcurrentHashMap<TimersRepository.TimerId, Session>()

    override suspend fun addMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId,
        dateTime: UnixTime
    ) {
        sessions.getOrPut(timerId) {
            Session()
        }.addMember(userId)
    }

    override suspend fun removeMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId
    ) {
        sessions[timerId]?.removeMember(userId)

        if (sessions[timerId]?.members?.isEmpty() == true)
            sessions.remove(timerId)
    }

    override suspend fun getMembers(
        timerId: TimersRepository.TimerId,
        afterUserId: UsersRepository.UserId?,
        count: Count
    ): List<UsersRepository.UserId> {
        return sessions[timerId]?.getMembers()
            ?.sortedBy(UsersRepository.UserId::int)
            ?.take(count.int)
            ?: emptyList()
    }

    @Suppress("unchecked")
    override suspend fun getActive(
        ids: List<TimersRepository.TimerId>
    ): Map<TimersRepository.TimerId, DetailedTimer.Active.SessionInfo> {
        return ids.associate { timerId ->
            val session = sessions[timerId] ?: return@associate timerId to null
            timerId to DetailedTimer.Active.SessionInfo(
                Count(session.members.size),
                when (session.state) {
                    State.CONFIRMATION -> DetailedTimer.Active.Phase.WAITING
                    State.RUNNING -> DetailedTimer.Active.Phase.RUNNING
                    State.WAITING -> DetailedTimer.Active.Phase.WAITING
                    State.PAUSED -> DetailedTimer.Active.Phase.PAUSE
                },
                session.stateEndsAt?.let { UnixTime(it) }
            )
        }.filterValues { it != null }
            as Map<TimersRepository.TimerId, DetailedTimer.Active.SessionInfo>
    }

    override suspend fun createConfirmation(timerId: TimersRepository.TimerId) {
        sessions[timerId]?.createConfirmation()
    }

    override suspend fun isConfirmationAvailable(timerId: TimersRepository.TimerId): Boolean {
        return sessions[timerId]?.isConfirmationAvailable() ?: false
    }

    override suspend fun confirm(timerId: TimersRepository.TimerId, userId: UsersRepository.UserId): Boolean {
        return sessions[timerId]?.confirm(userId) ?: false
    }

    private inner class Session(
        val members: MutableList<UsersRepository.UserId> = mutableListOf()
    ) {
        private val mutex = Mutex()
        var state: State = State.WAITING
        var stateEndsAt: Long? = null
        private val confirmedBy = mutableSetOf<UsersRepository.UserId>()

        fun createConfirmation() {
            state = State.CONFIRMATION
        }

        fun isConfirmationAvailable(): Boolean = state == State.CONFIRMATION

        suspend fun confirm(userId: UsersRepository.UserId): Boolean = mutex.withLock {
            confirmedBy += userId
            return@withLock if (members.containsAll(confirmedBy)) {
                state = State.RUNNING
                confirmedBy.clear()
                true
            } else false
        }

        suspend fun addMember(userId: UsersRepository.UserId) = mutex.withLock {
            members.add(userId)
        }

        suspend fun getMembers() = mutex.withLock { members }

        suspend fun removeMember(userId: UsersRepository.UserId) = mutex.withLock {
            members.remove(userId)
        }
    }

    private enum class State {
        CONFIRMATION, RUNNING, PAUSED, WAITING
    }

}