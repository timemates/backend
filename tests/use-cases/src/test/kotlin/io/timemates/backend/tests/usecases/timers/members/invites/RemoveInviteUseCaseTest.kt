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
import io.timemates.backend.usecases.timers.members.invites.RemoveInviteUseCase
import java.util.*

class RemoveInviteUseCaseTest {
    private val invitesRepo = InMemoryTimerInvitesRepository()
    private val timersRepo = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
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
                timeProvider.provide()
            )

            val code = SecureRandomStringProvider.provideInviteCode()
            invitesRepo.createInvite(id, code, timeProvider.provide(), Count(10))

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
                timeProvider.provide()
            )

            val code = SecureRandomStringProvider.provideInviteCode()
            invitesRepo.createInvite(id, code, timeProvider.provide(), Count(10))

            val result = useCase.invoke(
                UsersRepository.UserId(2),
                code
            )

            assert(result is RemoveInviteUseCase.Result.NoAccess)
            assert(invitesRepo.getInvite(code) != null)
        }
    }
}