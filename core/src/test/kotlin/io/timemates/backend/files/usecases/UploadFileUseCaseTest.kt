package io.timemates.backend.files.usecases

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.validation.createOrThrow
import com.timemates.random.SecureRandomProvider
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.timemates.backend.files.repositories.FilesRepository
import io.timemates.backend.files.types.FileType
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.testing.auth.testAuthContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.platform.commons.annotation.Testable
import kotlin.test.Test
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class UploadFileUseCaseTest {
    @MockK
    lateinit var filesRepository: FilesRepository

    lateinit var useCase: UploadFileUseCase

    private val randomProvider = SecureRandomProvider()

    private val timeProvider = SystemTimeProvider()

    private val fileType = mockk<FileType>()

    private val inputStream = mockk<Flow<ByteArray>>()

    private val fileId = mockk<FileId>()

    private val exception = mockk<Exception>()

    @BeforeAll
    fun before() {
        useCase = UploadFileUseCase(filesRepository, randomProvider, timeProvider)
    }

    @Test
    fun `test upload file`(): Unit = runBlocking {
        // GIVEN
        coEvery { FileId.createOrThrow(randomProvider.randomHash(FileId.SIZE)) } returns fileId
        coEvery { filesRepository.save(fileId, fileType, inputStream, timeProvider.provide()) } returns Unit
        // WHEN
        val result = testAuthContext { useCase.execute(fileType, inputStream) }

        // THEN
        assertEquals(
            expected = UploadFileUseCase.Result.Success(fileId),
            actual = result
        )
    }

    @Test
    fun `test failure of upload file`(): Unit = runBlocking {
        // GIVEN
        coEvery { FileId.createOrThrow(randomProvider.randomHash(FileId.SIZE)) } returns fileId
        coEvery { filesRepository.remove(fileId) } returns Unit
        coEvery { exception.printStackTrace() }

        // WHEN
        val result = testAuthContext { useCase.execute(fileType, inputStream) }

        // THEN
        assertEquals(
            expected = UploadFileUseCase.Result.Failure,
            actual = result
        )
    }
}