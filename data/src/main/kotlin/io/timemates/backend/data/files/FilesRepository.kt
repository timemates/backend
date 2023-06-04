package io.timemates.backend.data.files

import com.timemates.backend.time.UnixTime
import io.timemates.backend.data.files.datasource.FileEntityMapper
import io.timemates.backend.data.files.datasource.LocalFilesDataSource
import io.timemates.backend.data.files.datasource.PostgresqlFilesDataSource
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.FileType
import io.timemates.backend.files.types.value.FileId
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import kotlin.io.path.pathString
import io.timemates.backend.files.repositories.FilesRepository as FilesRepositoryContract


class FilesRepository(
    private val localFilesDataSource: LocalFilesDataSource,
    private val postgresqlFilesDataSource: PostgresqlFilesDataSource,
    private val mapper: FileEntityMapper,
) : FilesRepositoryContract {
    override suspend fun save(fileId: FileId, fileType: FileType, input: Flow<ByteArray>, creationTime: UnixTime) {
        postgresqlFilesDataSource.createFile(
            fileId = fileId.string,
            fileName = fileId.string,
            fileType = mapper.domainTypeToDbType(fileType),
            filePath = localFilesDataSource.localFilesPath.pathString,
            creationTime = creationTime.inMilliseconds,
        )

        localFilesDataSource.save(fileId.string, mapper.domainTypeToLocalFilesType(fileType), input)
    }

    override suspend fun retrieve(file: File): InputStream? {
        return localFilesDataSource.retrieve(file.fileId.string, LocalFilesDataSource.FileType.IMAGE)
    }

    override suspend fun remove(fileId: FileId) {
        localFilesDataSource.remove(fileId.string, LocalFilesDataSource.FileType.IMAGE)
    }

}