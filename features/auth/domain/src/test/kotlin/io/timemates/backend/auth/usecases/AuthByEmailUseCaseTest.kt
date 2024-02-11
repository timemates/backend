package io.timemates.backend.auth.usecases

import com.timemates.backend.time.TimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.random.RandomProvider
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.timemates.backend.auth.domain.repositories.EmailRepository
import io.timemates.backend.auth.domain.repositories.VerificationsRepository
import io.timemates.backend.auth.domain.usecases.AuthByEmailUseCase
import io.timemates.backend.foundation.validation.test.createOrAssert
import io.timemates.backend.types.auth.metadata.ClientMetadata
import io.timemates.backend.types.auth.metadata.value.ClientIpAddress
import io.timemates.backend.types.auth.metadata.value.ClientName
import io.timemates.backend.types.auth.metadata.value.ClientVersion
import io.timemates.backend.types.auth.value.Attempts
import io.timemates.backend.types.auth.value.VerificationHash
import io.timemates.backend.types.common.value.Count
import io.timemates.backend.types.users.value.EmailAddress
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.platform.commons.annotation.Testable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class AuthByEmailUseCaseTest {
    @MockK
    lateinit var verificationsRepository: VerificationsRepository

    @MockK
    lateinit var timeProvider: TimeProvider

    @MockK
    lateinit var emailsRepository: EmailRepository

    private val randomProvider: RandomProvider = mockk()

    private lateinit var useCase: AuthByEmailUseCase

    private val email = EmailAddress.createOrAssert("test@email.com")

    @BeforeAll
    fun before() {
        useCase = AuthByEmailUseCase(
            emailsRepository, verificationsRepository, timeProvider, randomProvider
        )
    }

    @Test
    fun `test success email sending`(): Unit = runTest {
        // GIVEN
        val clientMetadata = mockk<ClientMetadata>()
        val time = UnixTime.createOrAssert(System.currentTimeMillis())
        val hash = "A".repeat(128)
        val code = "12345678"

        coEvery { verificationsRepository.getNumberOfAttempts(any(), any()) } returns Result.success(Count.createOrAssert(0))
        coEvery { verificationsRepository.getNumberOfSessions(any(), any()) } returns Result.success(Count.createOrAssert(0))
        every { timeProvider.provide() } returns time
        coEvery { emailsRepository.send(any(), any()) } returns true
        coJustRun { verificationsRepository.save(any(), any(), any(), any(), any(), any()) }
        every { randomProvider.randomHash(eq(128)) } returns hash
        every { randomProvider.randomHash(eq(8)) } returns code

        // WHEN
        val result = useCase.execute(email, clientMetadata)

        // THEN
        assertEquals(
            expected = AuthByEmailUseCase.Result.Success(
                VerificationHash.createOrAssert(hash),
                time + 10.minutes,
                Attempts.createOrAssert(3)
            ),
            actual = result
        )
    }

    @Test
    fun `test sessions number exceed`(): Unit = runTest {
        // GIVEN
        val clientMetadata = ClientMetadata(
            clientName = ClientName.createOrAssert("name"),
            clientVersion = ClientVersion.createOrAssert(1.0),
            clientIpAddress = ClientIpAddress.createOrAssert("ip_address"),
        )
        coEvery { verificationsRepository.getNumberOfAttempts(any(), any()) } returns Result.success(Count.createOrAssert(0))
        coEvery { verificationsRepository.getNumberOfSessions(any(), any()) } returns Result.success(Count.createOrAssert(3))
        every { timeProvider.provide() } returns UnixTime.createOrAssert(System.currentTimeMillis())
        coJustRun { verificationsRepository.save(any(), any(), any(), any(), any(), any()) }

        // WHEN
        val result = useCase.execute(email, clientMetadata)

        // THEN
        assertEquals(
            expected = AuthByEmailUseCase.Result.AttemptsExceed,
            actual = result
        )
    }

    @Test
    fun `test attempts number exceed`(): Unit = runTest {
        // GIVEN
        val clientMetadata = mockk<ClientMetadata>()
        coEvery { verificationsRepository.getNumberOfSessions(any(), any()) } returns Result.success(Count.createOrAssert(1))
        coEvery { verificationsRepository.getNumberOfAttempts(any(), any()) } returns Result.success(Count.createOrAssert(9))
        every { timeProvider.provide() } returns UnixTime.createOrAssert(System.currentTimeMillis())
        coJustRun { verificationsRepository.save(any(), any(), any(), any(), any(), any()) }

        // WHEN
        val result = useCase.execute(email, clientMetadata)

        // THEN
        assertEquals(
            expected = AuthByEmailUseCase.Result.AttemptsExceed,
            actual = result,
        )
    }
}