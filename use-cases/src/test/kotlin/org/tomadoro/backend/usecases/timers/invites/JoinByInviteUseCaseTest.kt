package org.tomadoro.backend.usecases.timers.invites

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.providers.MockedCodeProvider
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.*
import org.tomadoro.backend.usecases.timers.members.invites.JoinByInviteUseCase

class JoinByInviteUseCaseTest {
    private val invitesRepo = MockedTimerInvitesRepository()
    private val timersRepo = MockedTimersRepository()
    private val timeProvider = MockedCurrentTimeProvider
    private val useCase = JoinByInviteUseCase(
        invitesRepo, timersRepo, timeProvider
    )

    @Test
    fun testSuccess() {
        runBlocking {
            val id = timersRepo.createTimer(
                TimerName("Test"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(1),
                MockedCurrentTimeProvider.provide()
            )

            val code = MockedCodeProvider.provide()
            invitesRepo.createInvite(id, code, TimerInvitesRepository.Count(5))

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                code
            )
            assert(result is JoinByInviteUseCase.Result.Success)
        }
    }

    @Test
    fun testNotFound() {
        runBlocking {
            val id = timersRepo.createTimer(
                TimerName("Test"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(2),
                MockedCurrentTimeProvider.provide()
            )

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                TimerInvitesRepository.Code("ANY")
            )

            assert(result is JoinByInviteUseCase.Result.NotFound)
        }
    }

    @Test
    fun testInviteDelete() {
        runBlocking {
            val id = timersRepo.createTimer(
                TimerName("Test"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(1),
                MockedCurrentTimeProvider.provide()
            )

            val code = MockedCodeProvider.provide()
            invitesRepo.createInvite(id, code, TimerInvitesRepository.Count(1))

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                code
            )

            assert(result is JoinByInviteUseCase.Result.Success)
            result as JoinByInviteUseCase.Result.Success
            assert(invitesRepo.getInvite(code) == null)
        }
    }
}