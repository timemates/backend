package org.timemates.backend.auth.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.TimeProvider
import com.timemates.random.SecureRandomProvider
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.repositories.UsersRepository
import org.timemates.backend.auth.domain.repositories.VerificationsRepository
import org.timemates.backend.auth.domain.usecases.ConfigureNewAccountUseCase
import org.timemates.backend.foundation.validation.test.createOrAssert
import org.timemates.backend.types.auth.Verification
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.value.Attempts
import org.timemates.backend.types.auth.value.AuthorizationId
import org.timemates.backend.types.auth.value.VerificationCode
import org.timemates.backend.types.auth.value.VerificationHash
import org.timemates.backend.types.users.value.EmailAddress
import org.timemates.backend.types.users.value.UserId
import org.timemates.backend.types.users.value.UserName
import kotlin.test.Test
import kotlin.test.assertIs

@MockKExtension.CheckUnnecessaryStub
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class ConfigureNewAccountUseCaseTest {
    @MockK
    lateinit var verificationsRepository: VerificationsRepository

    @MockK
    lateinit var authorizationsRepository: AuthorizationsRepository

    @MockK
    lateinit var usersRepository: UsersRepository

    private val timeProvider: TimeProvider = SystemTimeProvider()

    private val randomProvider = SecureRandomProvider()

    private lateinit var useCase: ConfigureNewAccountUseCase

    @BeforeAll
    fun before() {
        useCase = ConfigureNewAccountUseCase(
            usersRepository,
            authorizationsRepository,
            verificationsRepository,
            timeProvider,
            randomProvider
        )
    }

    @Test
    fun `check configure new account when everything is okay`(): Unit = runTest {
        // GIVEN
        val clientMetadata = mockk<ClientMetadata>()
        val userId = mockk<UserId>(relaxed = true)
        val email = EmailAddress.createOrAssert("test@email.com")
        val verificationHash = VerificationHash.createOrAssert(randomProvider.randomHash(
            VerificationHash.SIZE))
        coEvery { verificationsRepository.getVerification(any()) }
            .returns(
                Result.success(
                    Verification(
                        email,
                        VerificationCode.createOrAssert("1234F321"),
                        Attempts.createOrAssert(3),
                        timeProvider.provide(),
                        true,
                        clientMetadata,
                    )
                )
            )
        coEvery { verificationsRepository.remove(any()) } returns (Result.success(Unit))
        coEvery { authorizationsRepository.create(any(), any(), any(), any(), any(), any()) }
            .returns(AuthorizationId.createOrAssert(0))
        coEvery { usersRepository.create(any(), any(), any(), any()) }
            .returns(Result.success(userId))

        // WHEN
        val result = useCase.execute(
            verificationHash,
            UserName.createOrAssert("Test"),
            null
        )

        // THEN
        assertIs<ConfigureNewAccountUseCase.Result.Success>(value = result)
    }

    @Test
    fun `check configure returns not found when verification is not found in repo`(): Unit = runTest {
        // WHEN
        coEvery { verificationsRepository.getVerification(any()) } returns Result.success(null)

        // should fail as there is no such token
        assertIs<ConfigureNewAccountUseCase.Result.NotFound>(
            useCase.execute(
                mockk(relaxed = true),
                UserName.createOrAssert("test"), null
            )
        )
    }

    @Test
    fun `check configure returns not found when exception in repo`(): Unit = runTest {
        // WHEN
        coEvery { verificationsRepository.getVerification(any()) } returns Result.failure(mockk())

        // should fail as there is no such token
        assertIs<ConfigureNewAccountUseCase.Result.Failure>(
            useCase.execute(
                mockk(relaxed = true),
                UserName.createOrAssert("test"), null
            )
        )
    }
}