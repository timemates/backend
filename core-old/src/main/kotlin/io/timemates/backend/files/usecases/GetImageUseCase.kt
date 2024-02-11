package io.timemates.backend.files.usecases

import io.timemates.backend.common.markers.UseCase
import io.timemates.backend.files.repositories.FilesRepository
import io.timemates.backend.files.types.File
import java.io.InputStream

class GetImageUseCase(
    private val filesRepository: FilesRepository,
) : UseCase {
    suspend fun execute(file: File): Result {
        return when (val result = filesRepository.retrieve(file)) {
            null -> Result.NotFound
            else -> Result.Success(result)
        }
    }

    sealed interface Result {
        @JvmInline
        value class Success(val inputStream: InputStream) : Result
        data object NotFound : Result
    }
}