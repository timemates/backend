package io.timemates.backend.data.files.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.outputStream

class LocalFileDataSource(private val localFilesPath: Path) {
    suspend fun save(fileId: String, stream: InputStream) {
        withContext(Dispatchers.IO) {
            localFilesPath.fileSystem.getPath(fileId).createFile().outputStream().use {
                stream.copyTo(it)
            }
        }
    }

    suspend fun retrieve(fileId: String)
}