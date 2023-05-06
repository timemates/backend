package io.timemates.backend.data.files.datasource

import com.timemates.backend.validation.createOrThrow
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource.FileType
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.types.value.ImageSize
import io.timemates.backend.files.types.File as DomainFile


class FileEntityMapper {
    fun toDomainFile(
        fileId: String,
        fileType: FileType,
        imageSize: Int
    ): DomainFile {
        return when(fileType) {
            FileType.IMAGE -> DomainFile.Image(
                FileId.createOrThrow(fileId),
                ImageSize.createOrThrow(imageSize)
            )
        }
    }
}