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
import io.timemates.backend.usecases.timers.members.invites.JoinByInviteUseCase
import java.util.*

class JoinByInviteUseCaseTest {
    private val invitesRepo = InMemoryTimerInvitesRepository()
    private val timersRepo = InMemoryTimersRepository()
    private val timeProvider = SystemCurrentTimeProvider(TimeZone.getDefault())
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
                timeProvider.provide()
            )

            val code = SecureRandomStringProvider.provideInviteCode()
            invitesRepo.createInvite(id, code, timeProvider.provide(), Count(5))

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
                timeProvider.provide()
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
                timeProvider.provide()
            )

            val code = SecureRandomStringProvider.provideInviteCode()
            invitesRepo.createInvite(id, code, timeProvider.provide(), Count(1))

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