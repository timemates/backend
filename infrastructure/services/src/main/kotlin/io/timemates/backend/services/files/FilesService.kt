package io.timemates.backend.services.files

import io.timemates.api.common.types.StatusOuterClass
import io.timemates.api.files.FilesServiceGrpcKt
import io.timemates.api.files.requests.GetFileBytesRequestOuterClass
import io.timemates.api.files.requests.UploadFileRequest
import kotlinx.coroutines.flow.Flow

class FilesService : FilesServiceGrpcKt.FilesServiceCoroutineImplBase() {
    override fun getFileBytes(request: GetFileBytesRequestOuterClass.GetFileBytesRequest): Flow<GetFileBytesRequestOuterClass.GetFileBytesRequest.Response> {
        TODO()
    }

    override suspend fun uploadFile(requests: Flow<UploadFileRequest.FileChunk>): StatusOuterClass.Status {
        TODO()
    }
}