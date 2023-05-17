package io.timemates.backend.data.files

import io.timemates.backend.data.files.datasource.LocalFilesDataSource
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.value.FileId
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import io.timemates.backend.files.repositories.FilesRepository as FilesRepositoryContract


class FilesRepository(
    private val localFilesDataSource: LocalFilesDataSource,
    private val postgresqlFilesDataSource: PostgresqlFilesDataSource
) : FilesRepositoryContract {
    override suspend fun save(fileId: FileId, input: Flow<ByteArray>) {
        localFilesDataSource.save(fileId.string, LocalFilesDataSource.FileType.IMAGE, input)
    }

    override suspend fun retrieve(file: File): InputStream? {
        return localFilesDataSource.retrieve(file.fileId.string, LocalFilesDataSource.FileType.IMAGE)
    }

    override suspend fun remove(fileId: FileId) {
        localFilesDataSource.remove(fileId.string, LocalFilesDataSource.FileType.IMAGE)
    }

}