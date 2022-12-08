package org.tomadoro.backend.repositories.integration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tomadoro.backend.repositories.FilesRepository
import java.io.InputStream
import java.nio.file.Path
import java.util.*
import kotlin.io.path.*
import org.tomadoro.backend.repositories.FilesRepository as FilesRepositoryContract

class FilesRepository(private val rootPath: Path) : FilesRepositoryContract {
    override suspend fun save(stream: InputStream): FilesRepository.FileId {
        val fileId = UUID.randomUUID().toString().replace("-", "")
        withContext(Dispatchers.IO) {
            rootPath.fileSystem.getPath(fileId).createFile().outputStream().use {
                stream.copyTo(it)
            }
        }
        return FilesRepository.FileId(fileId)
    }

    override suspend fun retrieve(fileId: FilesRepository.FileId): InputStream? {
        return withContext(Dispatchers.IO) {
            rootPath.fileSystem.getPath(fileId.string)
                .takeIf { it.exists() }?.inputStream()
        }
    }

    override suspend fun remove(fileId: FilesRepository.FileId) {
        return withContext(Dispatchers.IO) {
            rootPath.fileSystem.getPath(fileId.string).deleteIfExists()
        }
    }
}