package io.timemates.backend.auth.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.random.SecureRandomProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.Authorization
import io.timemates.backend.authorization.types.metadata.ClientMetadata
import io.timemates.backend.authorization.types.metadata.value.ClientIpAddress
import io.timemates.backend.authorization.types.metadata.value.ClientName
import io.timemates.backend.authorization.types.metadata.value.ClientVersion
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.types.value.RefreshHash
import io.timemates.backend.authorization.usecases.RefreshTokenUseCase
import io.timemates.backend.testing.validation.createOrAssert
import io.timemates.backend.users.types.value.UserId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RefreshTokenUseCaseTest {

    private lateinit var useCase: RefreshTokenUseCase
    private val randomProvider = SecureRandomProvider()
    private val timeProvider = SystemTimeProvider()

    private val authorizationsRepository = mockk<AuthorizationsRepository>()

    @BeforeAll
    fun before() {
        MockKAnnotations.init(this)
        useCase = RefreshTokenUseCase(
            randomProvider = randomProvider,
            authorizations = authorizationsRepository,
            time = timeProvider
        )
    }

    @Test
    fun `refresh access token by valid access hash should pass`() = runBlocking {
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
                clientVersion = ClientVersion.createOrAssert(1.0),
                clientIpAddress = ClientIpAddress.createOrAssert("127.0.0.1"),
            )
        )
        coEvery { authorizationsRepository.renew(any(), any(), any()) }.returns(authorization)
        // WHEN
        val result = useCase.execute(refreshHash)
        // THEN
        assertEquals(RefreshTokenUseCase.Result.Success(accessHash), result)
    }

    @Test
    fun `refresh access token by invalid access hash should return InvalidAuthorization`() = runBlocking {
        // GIVEN
        val refreshHash = RefreshHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        coEvery { authorizationsRepository.renew(any(), any(), any()) }.returns(null)
        // WHEN
        val result = useCase.execute(refreshHash)
        // THEN
        assertEquals(RefreshTokenUseCase.Result.InvalidAuthorization, result)
    }
}