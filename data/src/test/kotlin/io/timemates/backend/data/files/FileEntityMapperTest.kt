package io.timemates.backend.data.files

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.files.datasource.FileEntityMapper
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.types.value.ImageSize
import kotlin.test.Test
import kotlin.test.assertEquals

class FileEntityMapperTest {
    private val mapper = FileEntityMapper()

    @Test
    fun `toDomainMapper should map to domain file correctly`() {
        val fileId = "123"
        val fileType = PostgresqlFilesDataSource.FileType.IMAGE
        val imageSize = 123

        val expectedFile = File.Image(
            FileId.createOrThrow(fileId),
            ImageSize.createOrThrow(imageSize)
        )

        val actualFile = mapper.toDomainFile(fileId, fileType, imageSize)

        assertEquals(
            expected = expectedFile,
            actual = actualFile
        )
    }
}