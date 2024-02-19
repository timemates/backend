package org.timemates.backend.auth.usecases

import com.timemates.random.SecureRandomProvider
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.annotation.Testable
import org.timemates.backend.auth.domain.repositories.AuthorizationsRepository
import org.timemates.backend.auth.domain.usecases.GetAuthorizationsUseCase
import org.timemates.backend.foundation.authorization.AuthDelicateApi
import org.timemates.backend.foundation.authorization.Authorized
import org.timemates.backend.foundation.authorization.types.AuthorizedId
import org.timemates.backend.pagination.Ordering
import org.timemates.backend.pagination.Page
import org.timemates.backend.pagination.PageToken
import kotlin.test.Test
import kotlin.test.assertEquals


@OptIn(AuthDelicateApi::class)
@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetAuthorizationsUseCaseTest {

    private lateinit var useCase: GetAuthorizationsUseCase
    private val randomProvider = SecureRandomProvider()

    @MockK
    private lateinit var authorizationsRepository: AuthorizationsRepository

    private val authorizedId = AuthorizedId(0)

    @BeforeEach
    fun before() {
        MockKAnnotations.init(this)
        useCase = GetAuthorizationsUseCase(
            authorizationsRepository = authorizationsRepository
        )
    }

    @Test
    fun `test success get authorizations`() = runTest {
        val pageToken = PageToken.toGive(randomProvider.randomHash(64))
        val nextPageToken = PageToken.toGive(randomProvider.randomHash(64))
        coEvery { authorizationsRepository.getList(any(), any()) }.returns(
            Page(emptyList(), nextPageToken, Ordering.ASCENDING)
        )
        val result: GetAuthorizationsUseCase.Result = useCase.execute(Authorized(authorizedId), pageToken)
        assertEquals(GetAuthorizationsUseCase.Result.Success(emptyList(), nextPageToken), result)
    }
}