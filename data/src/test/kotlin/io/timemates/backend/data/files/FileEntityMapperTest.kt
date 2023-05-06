package io.timemates.backend.data.files

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.files.datasource.FileEntityMapper
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.value.FileId
import kotlin.test.Test
import kotlin.test.assertEquals

class FileEntityMapperTest {
    private val mapper = FileEntityMapper()

    @Test
    fun `toDomainMapper should map to domain file correctly`() {
        val fileId = "123"
        val fileName = "Default Name"
        val fileType = PostgresqlFilesDataSource.FileType.IMAGE
        val filePath = "C\\123"
        val fileCreationType = 4444L

        val expectedFile = File.Image(
            FileId.createOrThrow(fileId)
        )

        val actualFile = mapper.toDomainFile(
            PostgresqlFilesDataSource.File(
                fileId,
                fileName,
                fileType, filePath,
                fileCreationType,
            )
        )

        assertEquals(
            expected = expectedFile,
            actual = actualFile
        )
    }
}