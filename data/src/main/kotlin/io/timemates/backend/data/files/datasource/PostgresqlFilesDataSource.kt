package io.timemates.backend.data.files.datasource

import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource.FilesTable.FILE_ID
import io.timemates.backend.exposed.suspendedTransaction
import org.jetbrains.annotations.TestOnly
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresqlFilesDataSource(private val database: Database, private val mapper: FileEntityMapper) {
    internal object FilesTable : Table("files") {
        val FILE_ID = text("file_id")
        val FILE_NAME = text("file_name")
        val FILE_TYPE = enumeration<FileType>("file_type")
        val CREATION_TIME = long("creation_time")
        val FILE_PATH = text("file_path")

        override val primaryKey: PrimaryKey = PrimaryKey(FILE_ID)
    }

    init {
        transaction(database) {
            SchemaUtils.create(FilesTable)
        }
    }

    suspend fun isFileExists(id: String): Boolean = suspendedTransaction(database) {
        FilesTable.select { FILE_ID eq id }.any()
    }

    suspend fun getFile(id: String): File? = suspendedTransaction(database) {
        FilesTable.select { FILE_ID eq id }.singleOrNull()?.let(mapper::resultRowToPSqlFile)
    }

    suspend fun createFile(fileId: String, fileName: String, fileType: FileType, filePath: String, creationTime: Long) =
        suspendedTransaction(database) {
            FilesTable.insert {
                it[FILE_ID] = fileId
                it[FILE_NAME] = fileName
                it[CREATION_TIME] = creationTime
                it[FILE_TYPE] = fileType
                it[FILE_PATH] = filePath
            }[FILE_ID]
        }

    suspend fun deleteFile(fileId: String) =
        suspendedTransaction(database) {
            FilesTable.deleteWhere {
                FILE_ID eq fileId
            }
        }

    data class File(
        val fileId: String,
        val fileName: String,
        val fileType: FileType,
        val filePath: String,
        val fileCreationTime: Long,
    )

    enum class FileType {
        IMAGE,
    }

    @TestOnly
    suspend fun clear() = suspendedTransaction(database) {
        FilesTable.deleteAll()
    }
}