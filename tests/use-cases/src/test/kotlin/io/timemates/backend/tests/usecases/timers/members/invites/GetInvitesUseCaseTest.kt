package io.timemates.backend.tests.usecases.timers.members.invites

import kotlinx.coroutines.runBlocking
import org.junit.Test
import io.timemates.backend.types.value.TimerName
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.provideInviteCode
import io.timemates.backend.repositories.*
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimerInvitesRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemoryTimersRepository
import io.timemates.backend.types.value.Count
import io.timemates.backend.usecases.timers.members.invites.GetInvitesUseCase
import java.util.*

class GetInvitesUseCaseTest {
    private val invitesRepo = InMemoryTimerInvitesRepository()
    private val timersRepo = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
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
                timeProvider.provide()
            )
            invitesRepo.createInvite(
                id,
                SecureRandomStringProvider.provideInviteCode(),
                timeProvider.provide(),
                Count(5)
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
                timeProvider.provide()
            )

            val result = useCase.invoke(
                UsersRepository.UserId(1),
                id
            )

            assert(result is GetInvitesUseCase.Result.NoAccess)
        }
    }
}