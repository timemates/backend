package io.timemates.backend.data.files.datasource

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource.FileType
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.types.File as DomainFile


class FileEntityMapper {
    fun toDomainFile(file: PostgresqlFilesDataSource.File): DomainFile {
        return when (file.fileType) {
            FileType.IMAGE -> DomainFile.Image(
                FileId.createOrThrow(file.fileId),
            )
        }
    }
}