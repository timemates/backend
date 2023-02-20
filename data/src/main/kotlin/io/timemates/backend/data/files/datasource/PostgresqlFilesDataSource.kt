package io.timemates.backend.data.files.datasource

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresqlFilesDataSource(private val database: Database) {
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

    suspend fun isFileExists(id: String): Boolean = newSuspendedTransaction(db = database) {
        FilesTable.select { FilesTable.FILE_ID eq id }.any()
    }

    suspend fun getFile(id: String): File? = newSuspendedTransaction(db = database) {
        FilesTable.select { FilesTable.FILE_ID eq id }.singleOrNull()?.toFile()
    }

    suspend fun createFile(fileId: String, fileName: String, fileType: FileType, filePath: String, creationTime: Long) =
        newSuspendedTransaction(db = database) {
            FilesTable.insert {
                it[FILE_ID] = fileId
                it[FILE_NAME] = fileName
                it[CREATION_TIME] = creationTime
                it[FILE_TYPE] = fileType
                it[FILE_PATH] = filePath
            }.resultedValues!!.single().toFile().fileId
        }

    suspend fun deleteFile(fileId: String) =
        newSuspendedTransaction(db = database) {
            FilesTable.deleteWhere {
                FILE_ID eq fileId
            }
        }

    data class File(
        val fileId: String,
        val fileName: String,
        val fileType: FileType,
        val filePath: String
    )

    private fun ResultRow.toFile(): File {
        return File(
            get(FilesTable.FILE_ID),
            get(FilesTable.FILE_NAME),
            get(FilesTable.FILE_TYPE),
            get(FilesTable.FILE_PATH)
        )
    }

    enum class FileType {
        IMAGE
    }
}