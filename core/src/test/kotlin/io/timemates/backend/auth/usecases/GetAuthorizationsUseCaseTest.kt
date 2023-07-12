package io.timemates.backend.auth.usecases

import com.timemates.random.SecureRandomProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.usecases.GetAuthorizationsUseCase
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.testing.auth.testAuthContext
import io.timemates.backend.users.types.value.userId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import kotlin.test.assertEquals

// todo correct tests
//@Testable
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAuthorizationsUseCaseTest {

    private lateinit var useCase: GetAuthorizationsUseCase
    private val randomProvider = SecureRandomProvider()

//    @MockK
    lateinit var authorizationsRepository: AuthorizationsRepository

    // @BeforeEach
    fun before() {
        MockKAnnotations.init(this)
        useCase = GetAuthorizationsUseCase(
            authorizationsRepository = authorizationsRepository
        )
    }

    // @Test
    fun `test success get authorizations`() = runBlocking {
        val pageToken = PageToken.withBase64(randomProvider.randomHash(64))
        val nextPageToken = PageToken.withBase64(randomProvider.randomHash(64))
        coEvery { authorizationsRepository.getList(any(), any()) }.returns(
            Page(emptyList(), nextPageToken, Ordering.ASCENDING)
        )
        val result: GetAuthorizationsUseCase.Result = testAuthContext { useCase.execute(pageToken) }
        assertEquals(GetAuthorizationsUseCase.Result.Success(emptyList(), nextPageToken), result)
    }
}