package io.timemates.backend.files.repositories

import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.types.value.ImageSize
import io.timemates.backend.users.types.value.ImageVariant
import java.io.InputStream
import java.io.OutputStream

interface FilesRepository {
    suspend fun saveImage(fileId: FileId, variant: ImageVariant, stream: OutputStream)
    suspend fun retrieve(file: File): InputStream?
    suspend fun remove(fileId: FileId)
}