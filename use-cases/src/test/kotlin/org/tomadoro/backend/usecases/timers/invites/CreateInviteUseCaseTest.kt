package org.tomadoro.backend.usecases.timers.invites

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.value.TimerName
import org.tomadoro.backend.providers.MockedCodeProvider
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.*
import org.tomadoro.backend.usecases.timers.members.invites.CreateInviteUseCase

class CreateInviteUseCaseTest {
    private val invitesRepo = MockedTimerInvitesRepository()
    private val timersRepo = MockedTimersRepository()
    private val useCase = CreateInviteUseCase(
        invitesRepo, timersRepo, MockedCodeProvider
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

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                id,
                TimerInvitesRepository.Count(2)
            )

            assert(result is CreateInviteUseCase.Result.Success)
        }
    }

    @Test
    fun testNoAccess() {
        runBlocking {
            val id = timersRepo.createTimer(
                TimerName("Test"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(2),
                MockedCurrentTimeProvider.provide()
            )

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                id,
                TimerInvitesRepository.Count(1)
            )

            assert(result is CreateInviteUseCase.Result.NoAccess)
        }
    }
}