package io.timemates.backend.rsocket.features.files

import io.timemates.backend.coroutines.asFlow
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.FileType
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.usecases.GetImageUseCase
import io.timemates.backend.files.usecases.UploadFileUseCase
import io.timemates.backend.rsocket.features.common.RSocketFailureCode
import io.timemates.backend.rsocket.features.common.providers.provideAuthorizationContext
import io.timemates.backend.rsocket.features.files.requests.GetFileRequest
import io.timemates.backend.rsocket.features.files.requests.UploadFileRequest
import io.timemates.backend.rsocket.internal.createOrFail
import io.timemates.backend.rsocket.internal.failRequest
import io.timemates.backend.rsocket.internal.markers.RSocketService
import io.timemates.backend.serializable.types.files.SerializableFileType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RSocketFilesService(
    private val getImageUseCase: GetImageUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
) : RSocketService {
    suspend fun getFile(request: GetFileRequest): Flow<GetFileRequest.Response> {
        val result = getImageUseCase.execute(File.Image(FileId.createOrFail(request.fileId)))

        return when (result) {
            is GetImageUseCase.Result.Success -> flow {
                emit(GetFileRequest.Response.Metadata(SerializableFileType.IMAGE))
                result.inputStream.asFlow().collect { bytes ->
                    emit(GetFileRequest.Response.Chunk(bytes))
                }
            }

            GetImageUseCase.Result.NotFound ->
                failRequest(RSocketFailureCode.NOT_FOUND, "File is not found.")
        }
    }

    suspend fun uploadFile(
        request: UploadFileRequest,
    ): UploadFileRequest.Response = provideAuthorizationContext {
        val fileType = when (request.fileType) {
            SerializableFileType.IMAGE -> FileType.IMAGE
        }

        val result = uploadFileUseCase.execute(fileType, request.bytes)

        return@provideAuthorizationContext when (result) {
            UploadFileUseCase.Result.Failure -> failRequest(
                failureCode = RSocketFailureCode.INTERNAL_SERVER_ERROR,
                message = "Failed to upload a file",
            )

            is UploadFileUseCase.Result.Success -> UploadFileRequest.Response(result.fileId.string)
        }
    }
}