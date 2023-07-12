package io.timemates.backend.auth.usecases

import com.timemates.random.SecureRandomProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.types.value.AccessHash
import io.timemates.backend.authorization.usecases.RemoveAccessTokenUseCase
import io.timemates.backend.testing.validation.createOrAssert
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RemoveAccessTokenUseCaseTest {

    private lateinit var useCase: RemoveAccessTokenUseCase
    private val randomProvider = SecureRandomProvider()

    @MockK
    lateinit var authorizationsRepository: AuthorizationsRepository

    @BeforeAll
    fun before() {
        MockKAnnotations.init(this)
        useCase = RemoveAccessTokenUseCase(authorizationsRepository)
    }

    @Test
    fun `test success remove token`() = runBlocking {
        // GIVEN
        val accessHash = AccessHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        coEvery { authorizationsRepository.remove(accessHash) }.returns(true)
        // WHEN
        val result = useCase.execute(accessHash)
        // THEN
        assertEquals(RemoveAccessTokenUseCase.Result.Success, result)
    }

    @Test
    fun `test failed remove token, access hash is wrong`() = runBlocking {
        // GIVEN
        val accessHash = AccessHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        coEvery { authorizationsRepository.remove(any()) }.returns(false)
        // WHEN
        val result = useCase.execute(accessHash)
        // THEN
        assertEquals(RemoveAccessTokenUseCase.Result.AuthorizationNotFound, result)
    }
}