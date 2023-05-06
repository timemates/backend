package io.timemates.backend.data.files.datasource

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource.FileType
import io.timemates.backend.files.types.value.FileId
import org.jetbrains.exposed.sql.ResultRow
import io.timemates.backend.files.types.File as DomainFile


class FileEntityMapper {
    fun toDomainFile(file: PostgresqlFilesDataSource.File): DomainFile {
        return when (file.fileType) {
            FileType.IMAGE -> DomainFile.Image(
                FileId.createOrThrow(file.fileId),
            )
        }
    }

    fun resultRowToPSqlFile(resultRow: ResultRow): PostgresqlFilesDataSource.File = with(resultRow) {
        return@with PostgresqlFilesDataSource.File(
            get(PostgresqlFilesDataSource.FilesTable.FILE_ID),
            get(PostgresqlFilesDataSource.FilesTable.FILE_NAME),
            get(PostgresqlFilesDataSource.FilesTable.FILE_TYPE),
            get(PostgresqlFilesDataSource.FilesTable.FILE_PATH),
            get(PostgresqlFilesDataSource.FilesTable.CREATION_TIME),
        )
    }
}