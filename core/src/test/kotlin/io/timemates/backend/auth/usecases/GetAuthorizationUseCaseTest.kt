package io.timemates.backend.auth.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.random.SecureRandomProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.authorization.usecases.GetAuthorizationUseCase
import io.timemates.backend.testing.validation.createOrAssert
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAuthorizationUseCaseTest {

    private lateinit var useCase: GetAuthorizationUseCase
    private val timeProvider = SystemTimeProvider()
    private val randomProvider = SecureRandomProvider()

    @MockK
    lateinit var authorizationsRepository: AuthorizationsRepository

    @BeforeEach
    fun before() {
        MockKAnnotations.init(this)
        useCase = GetAuthorizationUseCase(
            authorizationsRepository = authorizationsRepository,
            timerProvider = timeProvider
        )
    }

    @Test
    fun `test success get authorization`() = runBlocking {
        // GIVEN
        val accessHashValue = randomProvider.randomHash(AccessHash.SIZE)
        val accessHash = AccessHash.createOrAssert(accessHashValue)
        val refreshHash = RefreshHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        val authorization = Authorization(
            userId = UserId.createOrAssert(1235),
            accessHash = accessHash,
            refreshAccessHash = refreshHash,
            scopes = listOf(),
            createdAt = UnixTime.createOrAssert(12323232),
            expiresAt = UnixTime.createOrAssert(12323235),
            clientMetadata = ClientMetadata(
                clientName = ClientName.createOrAssert("Xiaomi"),
                clientVersion = ClientVersion.createOrAssert("11"),
                clientIpAddress = ClientIpAddress.createOrAssert("127.0.0.1"),
            )
        )
        coEvery { authorizationsRepository.get(any(), any()) }.returns(authorization)
        // WHEN
        val result = useCase.execute(accessHash)
        // THEN
        assertEquals(GetAuthorizationUseCase.Result.Success(authorization), result)
    }

    @Test
    fun `test failed get authorization, user was not found`() = runBlocking {
        // GIVEN
        val accessHash = AccessHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        coEvery { authorizationsRepository.get(any(), any()) }.returns(null)
        // WHEN
        val result = useCase.execute(accessHash)
        // THEN
        assertEquals(GetAuthorizationUseCase.Result.NotFound, result)
    }
}