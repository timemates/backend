package org.tomadoro.backend.usecases.timers.invites

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.tomadoro.backend.domain.TimerName
import org.tomadoro.backend.providers.MockedCodeProvider
import org.tomadoro.backend.providers.MockedCurrentTimeProvider
import org.tomadoro.backend.repositories.*

class GetInvitesUseCaseTest {
    private val invitesRepo = MockedTimerInvitesRepository()
    private val timersRepo = MockedTimersRepository()
    private val useCase = GetInvitesUseCase(
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
            invitesRepo.createInvite(
                id,
                MockedCodeProvider.provide(),
                TimerInvitesRepository.Count(5)
            )

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                id
            )
            assert(result is GetInvitesUseCase.Result.Success)
            result as GetInvitesUseCase.Result.Success
            assert(result.list.size == 1)
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
                id
            )

            assert(result is GetInvitesUseCase.Result.NoAccess)
        }
    }
}