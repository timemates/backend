package org.timemates.backend.auth.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.UnixTime
import com.timemates.random.SecureRandomProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.usecases.RefreshAccessTokenUseCase
import org.timemates.backend.foundation.validation.test.createOrAssert
import org.timemates.backend.types.auth.Authorization
import org.timemates.backend.types.auth.metadata.ClientMetadata
import org.timemates.backend.types.auth.metadata.value.ClientIpAddress
import org.timemates.backend.types.auth.metadata.value.ClientName
import org.timemates.backend.types.auth.metadata.value.ClientVersion
import org.timemates.backend.types.auth.value.AccessHash
import org.timemates.backend.types.auth.value.RefreshHash
import org.timemates.backend.types.users.value.UserId
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RefreshTokenUseCaseTest {

    private lateinit var useCase: RefreshAccessTokenUseCase
    private val randomProvider = SecureRandomProvider()
    private val timeProvider = SystemTimeProvider()

    private val authorizationsRepository = mockk<AuthorizationsRepository>()

    @BeforeAll
    fun before() {
        useCase = RefreshAccessTokenUseCase(
            randomProvider = randomProvider,
            authorizations = authorizationsRepository,
            time = timeProvider
        )
    }

    @Test
    fun `refresh access token by valid access hash should pass`() = runTest {
        // GIVEN
        val accessHashValue = randomProvider.randomHash(AccessHash.SIZE)
        val accessHash = AccessHash.createOrAssert(accessHashValue)
        val refreshHash = RefreshHash.createOrAssert(randomProvider.randomHash(
            AccessHash.SIZE))
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
        assertEquals(RefreshAccessTokenUseCase.Result.Success(authorization), result)
    }

    @Test
    fun `refresh access token by invalid access hash should return InvalidAuthorization`() = runTest {
        // GIVEN
        val refreshHash = RefreshHash.createOrAssert(randomProvider.randomHash(
            AccessHash.SIZE))
        coEvery { authorizationsRepository.renew(any(), any(), any()) }.returns(null)
        // WHEN
        val result = useCase.execute(refreshHash)
        // THEN
        assertEquals(RefreshAccessTokenUseCase.Result.InvalidAuthorization, result)
    }
}