package io.timemates.backend.files.usecases

import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.timemates.backend.files.repositories.FilesRepository
import io.timemates.backend.files.types.File
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.platform.commons.annotation.Testable
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals

@Testable
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
class GetImageUseCaseTest {
    @MockK
    lateinit var filesRepository: FilesRepository

    lateinit var useCase: GetImageUseCase

    private val file = mockk<File.Image>()

    private val inputStream = mockk<InputStream>()

    @BeforeAll
    fun before() {
        useCase = GetImageUseCase(filesRepository)
    }

    @Test
    fun `test get image`(): Unit = runBlocking {
        // GIVEN
        coEvery { filesRepository.retrieve(file) } returns inputStream

        // WHEN
        val result = useCase.execute(file)

        // THEN
        assertEquals(
            expected = GetImageUseCase.Result.Success(inputStream),
            actual = result
        )
    }

    @Test
    fun `test get not found result from get image`(): Unit = runBlocking {
        // GIVEN
        coEvery { filesRepository.retrieve(file) } returns null

        // WHEN
        val result = useCase.execute(file)

        // THEN
        assertEquals(
            expected = GetImageUseCase.Result.NotFound,
            actual = result
        )
    }
}