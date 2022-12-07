package org.tomadoro.backend.repositories.integration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tomadoro.backend.repositories.SessionsRepository
import org.tomadoro.backend.repositories.TimersRepository
import org.tomadoro.backend.repositories.UsersRepository
import java.util.concurrent.ConcurrentHashMap
import org.tomadoro.backend.repositories.SessionsRepository as Contract

class SessionsRepository(
    private val timersRepository: TimersRepository,
    private val schedulerScope: CoroutineScope
) : Contract {
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

    override suspend fun getMembers(timerId: TimersRepository.TimerId): List<UsersRepository.UserId> {
        return sessions[timerId]?.getMembers() ?: emptyList()
    }

    override suspend fun updatesOf(timerId: TimersRepository.TimerId): Flow<Contract.Update> {
        return sessions[timerId]?.updates ?: emptyFlow()
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
        sessions[timerId]?.sendUpdate(update)
    }

    private inner class Session(
        val members: MutableList<UsersRepository.UserId> = mutableListOf(),
        val updates: MutableSharedFlow<Contract.Update> =
            MutableSharedFlow(0)
    ) {
        private val mutex = Mutex()
        private var isConfirmationState: Boolean = false
        private val confirmedBy = mutableSetOf<UsersRepository.UserId>()

        fun createConfirmation() {
            isConfirmationState = true
        }

        fun isConfirmationAvailable(): Boolean = isConfirmationState

        suspend fun confirm(userId: UsersRepository.UserId): Boolean = mutex.withLock {
            confirmedBy += userId
            return@withLock if (members.containsAll(confirmedBy)) {
                isConfirmationState = false
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

}