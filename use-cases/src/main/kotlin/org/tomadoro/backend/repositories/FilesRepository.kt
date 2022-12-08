package org.tomadoro.backend.repositories

import java.io.InputStream

interface FilesRepository {
    suspend fun save(stream: InputStream): FileId
    suspend fun retrieve(fileId: FileId): InputStream?
    suspend fun remove(fileId: FileId)

    @JvmInline
    value class FileId(val string: String)
}