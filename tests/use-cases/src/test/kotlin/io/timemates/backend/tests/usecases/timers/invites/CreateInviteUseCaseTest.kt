package io.timemates.backend.tests.usecases.timers.invites

import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.repositories.*
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimerInvitesRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.usecases.timers.members.invites.CreateInviteUseCase
import java.util.*

class CreateInviteUseCaseTest {
    private val invitesRepo = InMemoryTimerInvitesRepository()
    private val timersRepo = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
    private val useCase = CreateInviteUseCase(
        invitesRepo, timersRepo, SecureRandomStringProvider
    )

    @Test
    fun testSuccess() {
        runBlocking {
            val id = timersRepo.createTimer(
                TimerName("Test"),
                TimersRepository.Settings.Default,
                UsersRepository.UserId(1),
                timeProvider.provide()
            )

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                id,
                Count(2)
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
                timeProvider.provide()
            )

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                id,
                Count(1)
            )

            assert(result is CreateInviteUseCase.Result.NoAccess)
        }
    }
}