package io.timemates.backend.data.files.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.*

class LocalFilesDataSource(private val localFilesPath: Path) {

    enum class FileType {
        IMAGE
    }

    suspend fun save(fileId: String, fileType: FileType, inputStream: InputStream) {
        withContext(Dispatchers.IO) {
            localFilesPath
                .fileSystem
                .getPath(fileType.name.lowercase())
                .fileSystem
                .getPath(fileId)
                .createFile()
                .outputStream()
                .use { inputStream.copyTo(it) }
        }
    }

    suspend fun retrieve(fileId: String, fileType: FileType): InputStream? {
        return withContext(Dispatchers.IO) {
            localFilesPath.fileSystem
                .getPath(fileType.name.lowercase())
                .fileSystem
                .getPath(fileId)
                .takeIf { it.exists() }
                ?.inputStream()
        }
    }

    suspend fun remove(fileId: String, fileType: FileType) {
        return withContext(Dispatchers.IO) {
            localFilesPath
                .fileSystem.getPath(fileType.name.lowercase())
                .fileSystem.getPath(fileId)
                .deleteIfExists()
        }
    }
}