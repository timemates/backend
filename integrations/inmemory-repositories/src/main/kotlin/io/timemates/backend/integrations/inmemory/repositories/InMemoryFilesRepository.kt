package io.timemates.backend.integrations.inmemory.repositories

import io.timemates.backend.repositories.FilesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID

class InMemoryFilesRepository : FilesRepository {
    private val files = mutableMapOf<FilesRepository.FileId, ByteArray>()

    override suspend fun save(stream: InputStream): FilesRepository.FileId {
        val id = FilesRepository.FileId(UUID.randomUUID().toString())
        files[id] = withContext(Dispatchers.IO) {
            stream.readAllBytes()
        }

        return id
    }

    override suspend fun retrieve(fileId: FilesRepository.FileId): InputStream? {
        return files[fileId]?.inputStream()
    }

    override suspend fun remove(fileId: FilesRepository.FileId) {
        files.remove(fileId)
    }
}