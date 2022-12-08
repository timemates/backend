package org.tomadoro.backend.usecases.files

import org.tomadoro.backend.repositories.FilesRepository
import java.io.InputStream

class GetFileUseCase(
    private val filesRepository: FilesRepository
) {
    suspend operator fun invoke(fileId: FilesRepository.FileId): Result {
        return when(val result = filesRepository.retrieve(fileId)) {
            null -> Result.NotFound
            else -> Result.Success(result)
        }
    }

    sealed interface Result {
        @JvmInline
        value class Success(val inputStream: InputStream) : Result
        object NotFound : Result
    }
}