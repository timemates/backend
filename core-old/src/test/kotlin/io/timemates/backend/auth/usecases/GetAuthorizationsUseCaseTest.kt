package io.timemates.backend.auth.usecases

import com.timemates.random.SecureRandomProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.usecases.GetAuthorizationsUseCase
import io.timemates.backend.pagination.Ordering
import io.timemates.backend.pagination.Page
import io.timemates.backend.pagination.PageToken
import io.timemates.backend.testing.auth.testAuthContext
import kotlinx.coroutines.runBlocking
import kotlin.test.assertEquals

// todo correct tests
//@Testable
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAuthorizationsUseCaseTest {

    private lateinit var useCase: GetAuthorizationsUseCase
    private val randomProvider = SecureRandomProvider()

//    @MockK
private lateinit var authorizationsRepository: AuthorizationsRepository

    // @BeforeEach
    fun before() {
        MockKAnnotations.init(this)
        useCase = GetAuthorizationsUseCase(
            authorizationsRepository = authorizationsRepository
        )
    }

    // @Test
    fun `test success get authorizations`() = runBlocking {
        val pageToken = PageToken.toGive(randomProvider.randomHash(64))
        val nextPageToken = PageToken.toGive(randomProvider.randomHash(64))
        coEvery { authorizationsRepository.getList(any(), any()) }.returns(
            Page(emptyList(), nextPageToken, Ordering.ASCENDING)
        )
        val result: GetAuthorizationsUseCase.Result = testAuthContext { useCase.execute(pageToken) }
        assertEquals(GetAuthorizationsUseCase.Result.Success(emptyList(), nextPageToken), result)
    }
}