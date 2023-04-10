package io.timemates.backend.auth.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.TimeProvider
import com.timemates.backend.validation.createOrThrow
import com.timemates.random.SecureRandomProvider
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.types.Verification
import io.timemates.backend.authorization.types.value.Attempts
import io.timemates.backend.authorization.types.value.VerificationCode
import io.timemates.backend.authorization.types.value.VerificationHash
import io.timemates.backend.authorization.usecases.ConfigureNewAccountUseCase
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.types.value.EmailAddress
import io.timemates.backend.users.types.value.UserId
import io.timemates.backend.users.types.value.UserName
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.Test
import kotlin.test.assertIs

//@MockKExtension.CheckUnnecessaryStub
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(MockKExtension::class)
class ConfigureNewAccountUseCaseTest {
    @MockK
    lateinit var verificationsRepository: VerificationsRepository

    @MockK
    lateinit var authorizationsRepository: AuthorizationsRepository

    @MockK
    lateinit var usersRepository: UsersRepository

    private val timeProvider: TimeProvider = SystemTimeProvider()

    private val randomProvider = SecureRandomProvider()

    lateinit var useCase: ConfigureNewAccountUseCase

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

    // todo fix when mockk will support value classes in coEvery
    //@Test
    fun `test configure new account`(): Unit = runBlocking {
        // GIVEN
        val userId = mockk<UserId>(relaxed = true)
        val email = EmailAddress.createOrThrow("test@email.com")
        val verificationHash = VerificationHash.createOrThrow(randomProvider.randomHash(VerificationHash.SIZE))
        coEvery { verificationsRepository.getVerification(any()) }
            .returns(
                Verification(
                    email,
                    VerificationCode.createOrThrow("1234F"),
                    Attempts.createOrThrow(3),
                    timeProvider.provide(),
                    true
                )
            )
        coEvery { usersRepository.createUser(any(), any(), any(), any()) }
            .returns(userId)

        // WHEN
        val result = useCase.execute(
            verificationHash,
            UserName.createOrThrow("Test"),
            null
        )

        // THEN
        assertIs<ConfigureNewAccountUseCase.Result.Success>(value = result)
    }

//    @Test
//    fun testNotFound(): Unit = runBlocking {
//
//
//
//        // operation should fail as verification wasn't confirmed
//        assert(
//            useCase.invoke(
//                token, UserName("test"), null
//            ) is ConfigureNewAccountUseCase.Result.NotFound
//        )
//
//        // should fail as there is no such token
//        assertEquals(
//            actual = useCase.invoke(
//                VerificationHash.createOrThrow("12345"),
//                UserName.createOrThrow("test"), null
//            ) is ConfigureNewAccountUseCase.Result.NotFound
//        )
//    }
}