package io.timemates.backend.files.repositories

import com.timemates.backend.time.UnixTime
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.FileType
import io.timemates.backend.files.types.value.FileId
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface FilesRepository {
    suspend fun save(fileId: FileId, fileType: FileType, input: Flow<ByteArray>, creationTime: UnixTime)
    suspend fun retrieve(file: File): InputStream?
    suspend fun remove(fileId: FileId)
}