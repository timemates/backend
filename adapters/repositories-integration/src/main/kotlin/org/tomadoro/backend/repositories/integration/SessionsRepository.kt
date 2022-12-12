package org.tomadoro.backend.repositories.integration

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tomadoro.backend.domain.Count
import org.tomadoro.backend.domain.DateTime
import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import org.tomadoro.backend.usecases.timers.types.DetailedTimer
import java.util.concurrent.ConcurrentHashMap
import org.tomadoro.backend.repositories.SessionsRepository as Contract

class SessionsRepository : Contract {
    private val sessions = ConcurrentHashMap<TimersRepository.TimerId, Session>()

    override suspend fun addMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId
    ): Unit {
        sessions.getOrPut(timerId) {
            Session()
        }.addMember(userId)
    }

    override suspend fun removeMember(
        timerId: TimersRepository.TimerId,
        userId: UsersRepository.UserId
    ): Unit {
        sessions[timerId]?.removeMember(userId)

        if (sessions[timerId]!!.members.isEmpty())
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

    override suspend fun updatesOf(timerId: TimersRepository.TimerId): Flow<Contract.Update> {
        return sessions[timerId]?.updates ?: emptyFlow()
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
                session.stateEndsAt?.let { DateTime(it) }
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

    override suspend fun sendUpdate(
        timerId: TimersRepository.TimerId,
        update: Contract.Update
    ) {
        val session = sessions[timerId]
        if (session != null) {
            session.sendUpdate(update)
            session.state = when (update) {
                is Contract.Update.TimerStarted -> State.RUNNING
                is Contract.Update.TimerStopped ->
                    if (update.startsAt != null) State.PAUSED else State.WAITING

                is SessionsRepository.Update.Confirmation ->
                    State.WAITING

                else -> session.state
            }
        }

    }

    private inner class Session(
        val members: MutableList<UsersRepository.UserId> = mutableListOf(),
        val updates: MutableSharedFlow<Contract.Update> =
            MutableSharedFlow(0)
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

        suspend fun sendUpdate(update: SessionsRepository.Update) {
            updates.emit(update)
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