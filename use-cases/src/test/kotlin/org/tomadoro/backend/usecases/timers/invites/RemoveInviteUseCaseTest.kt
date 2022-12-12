package org.tomadoro.backend.usecases.timers.invites

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.providers.MockedCodeProvider
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.*
import org.tomadoro.backend.usecases.timers.members.invites.RemoveInviteUseCase

class RemoveInviteUseCaseTest {
    private val invitesRepo = MockedTimerInvitesRepository()
    private val timersRepo = MockedTimersRepository()
    private val useCase = RemoveInviteUseCase(
        invitesRepo, timersRepo
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
            invitesRepo.createInvite(id, code, TimerInvitesRepository.Count(10))

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                code
            )

            assert(result is RemoveInviteUseCase.Result.Success)
            assert(invitesRepo.getInvite(code) == null)
        }
    }

    @Test
    fun testNoAccess() {
        runBlocking {
            val id = timersRepo.createTimer(
                TimerName("Test"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(1),
                MockedCurrentTimeProvider.provide()
            )

            val code = MockedCodeProvider.provide()
            invitesRepo.createInvite(id, code, TimerInvitesRepository.Count(10))

            val result = useCase.invoke(
                UsersRepository.UserId(2),
                code
            )

            assert(result is RemoveInviteUseCase.Result.NoAccess)
            assert(invitesRepo.getInvite(code) != null)
        }
    }
}