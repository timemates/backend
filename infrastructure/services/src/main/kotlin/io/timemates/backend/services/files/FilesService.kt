package io.timemates.backend.services.files

import com.google.protobuf.kotlin.toByteString
import com.timemates.backend.validation.createOrThrow
import io.grpc.Status
import io.grpc.StatusException
import io.timemates.api.files.FilesServiceGrpcKt
import io.timemates.api.files.requests.GetFileBytesRequestKt
import io.timemates.api.files.requests.GetFileBytesRequestOuterClass.GetFileBytesRequest
import io.timemates.api.files.requests.UploadFileRequestKt
import io.timemates.api.files.requests.UploadFileRequestOuterClass
import io.timemates.api.files.requests.UploadFileRequestOuterClass.UploadFileRequest.FileMetadata
import io.timemates.api.files.requests.metadataOrNull
import io.timemates.backend.coroutines.asFlow
import io.timemates.backend.files.types.File
import io.timemates.backend.files.types.FileType
import io.timemates.backend.files.types.value.FileId
import io.timemates.backend.files.usecases.GetImageUseCase
import io.timemates.backend.files.usecases.UploadFileUseCase
import io.timemates.backend.services.authorization.context.provideAuthorizationContext
import kotlinx.coroutines.flow.*

class FilesService(
    private val getImageUseCase: GetImageUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
) : FilesServiceGrpcKt.FilesServiceCoroutineImplBase() {
    override fun getFileBytes(
        request: GetFileBytesRequest,
    ): Flow<GetFileBytesRequest.Response> = flow {
        when (val result = getImageUseCase.execute(File.Image(FileId.createOrThrow(request.fileId)))) {
            GetImageUseCase.Result.NotFound -> throw StatusException(Status.NOT_FOUND)
            is GetImageUseCase.Result.Success -> {
                result.inputStream
                    .asFlow()
                    .map { bytes ->
                        GetFileBytesRequestKt.response { chunk = bytes.toByteString() }
                    }
                    .collect { emit(it) }
            }
        }
    }

    override suspend fun uploadFile(
        requests: Flow<UploadFileRequestOuterClass.UploadFileRequest>,
    ): UploadFileRequestOuterClass.UploadFileRequest.Response = provideAuthorizationContext {
        val fileMetadata = requests.first().metadataOrNull
            ?: throw StatusException(Status.INVALID_ARGUMENT.withDescription("No metadata provided"))
        val fileBytes = requests.filter { it.hasChunk() }.map { it.chunk.toByteArray() }

        if(fileMetadata.fileType == FileMetadata.FileType.BINARY)
            throw StatusException(Status.UNIMPLEMENTED.withDescription("Binary file is unsupported"))

        when (val result = uploadFileUseCase.execute(FileType.IMAGE, fileBytes)) {
            UploadFileUseCase.Result.Failure -> throw StatusException(Status.INTERNAL)
            is UploadFileUseCase.Result.Success -> UploadFileRequestKt.response {
                fileId = result.fileId.string
            }
        }
    }
}