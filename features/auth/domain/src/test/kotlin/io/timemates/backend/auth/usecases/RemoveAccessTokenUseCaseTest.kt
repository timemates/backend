package io.timemates.backend.auth.usecases

import com.timemates.random.SecureRandomProvider
import io.mockk.coEvery
import io.mockk.mockk
import io.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import io.timemates.backend.auth.domain.usecases.RemoveAccessTokenUseCase
import io.timemates.backend.foundation.validation.test.createOrAssert
import io.timemates.backend.types.auth.value.AccessHash
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RemoveAccessTokenUseCaseTest {

    private lateinit var useCase: RemoveAccessTokenUseCase
    private val randomProvider = SecureRandomProvider()

    private val authorizationsRepository = mockk<AuthorizationsRepository>()

    @BeforeAll
    fun before() {
        useCase = RemoveAccessTokenUseCase(authorizationsRepository)
    }

    @Test
    fun `remove current valid authorization should pass`() = runTest {
        // GIVEN
        val accessHash = AccessHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        coEvery { authorizationsRepository.remove(accessHash) }.returns(true)
        // WHEN
        val result = useCase.execute(accessHash)
        // THEN
        assertEquals(RemoveAccessTokenUseCase.Result.Success, result)
    }

    @Test
    fun `remove current authorization by invalid access hash should fail`() = runTest {
        // GIVEN
        val accessHash = AccessHash.createOrAssert(randomProvider.randomHash(AccessHash.SIZE))
        coEvery { authorizationsRepository.remove(any()) }.returns(false)
        // WHEN
        val result = useCase.execute(accessHash)
        // THEN
        assertEquals(RemoveAccessTokenUseCase.Result.AuthorizationNotFound, result)
    }
}