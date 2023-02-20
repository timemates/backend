package io.timemates.backend.files.repositories

import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.value.FileId
import java.io.InputStream

interface FilesRepository {
    suspend fun save(fileId: FileId, input: InputStream)
    suspend fun retrieve(file: File): InputStream?
    suspend fun remove(fileId: FileId)
}