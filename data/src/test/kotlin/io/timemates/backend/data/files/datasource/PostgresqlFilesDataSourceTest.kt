package io.timemates.backend.data.files.datasource

import com.mongodb.assertions.Assertions.assertTrue
import com.timemates.random.SecureRandomProvider
import io.timemates.backend.files.types.value.FileId
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class PostgresqlFilesDataSourceTest {
    private val random = SecureRandomProvider()
    private val databaseUrl = "jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;"
    private val databaseDriver = "org.h2.Driver"

    private val fileCreationTime = 4444L
    private val fileId = random.randomHash(FileId.SIZE)
    private val fileName = "DefaultFile"
    private val filePath = "C\\123"
    private val fileType = PostgresqlFilesDataSource.FileType.IMAGE

    private val database = Database.connect(databaseUrl, databaseDriver)
    private val datasource = PostgresqlFilesDataSource(database)

    @BeforeTest
    fun beforeEach(): Unit = runBlocking {
        datasource.clear()
    }

    @Test
    fun `isFileExists should return true if file exists`(): Unit = runBlocking {
        val fileId = datasource.createFile(fileId, fileName, fileType, filePath, fileCreationTime)
        val result = datasource.isFileExists(fileId)
        assertTrue(result)
    }

    @Test
    fun `isFileExists should return false if file does not exist`() {
        val result = runBlocking { datasource.isFileExists(fileId) }
        assertFalse(result)
    }

    @Test
    fun `getFile should return file if file with id exists`() = runBlocking {
        val fileId = datasource.createFile(fileId, fileName, fileType, filePath, fileCreationTime)
        val expectedFile = PostgresqlFilesDataSource.File(
            fileId = fileId,
            fileName = fileName,
            fileType = fileType,
            filePath = filePath,
            fileCreationTime = fileCreationTime
        )

        val result = datasource.getFile(fileId)
        assertEquals(
            expected = expectedFile,
            actual = result
        )
    }

    @Test
    fun `getFile should return null if file with id does not exist`() {
        val result = runBlocking { datasource.getFile(fileId) }
        assertNull(result)
    }
}