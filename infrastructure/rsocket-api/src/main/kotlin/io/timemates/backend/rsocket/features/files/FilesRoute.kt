package io.timemates.backend.rsocket.features.files

import com.y9vad9.rsocket.router.builders.RoutingBuilder
import com.y9vad9.rsocket.router.builders.requestChannel
import com.y9vad9.rsocket.router.builders.requestStream
import io.ktor.utils.io.core.*
import io.timemates.backend.rsocket.features.files.requests.GetFileRequest
import io.timemates.backend.rsocket.features.files.requests.UploadFileRequest
import io.timemates.backend.rsocket.internal.asPayload
import io.timemates.backend.rsocket.internal.decodeFromJson
import io.timemates.backend.rsocket.internal.decodingForFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

fun RoutingBuilder.files(service: RSocketFilesService): Unit = route("files") {
    requestStream("get") { payload ->
        payload.decodingForFlow<GetFileRequest> {
            service.getFile(it).map(GetFileRequest.Response::asPayload)
        }
    }

    requestChannel("upload") { _, payloads ->
        val metadata = payloads.first().decodeFromJson<UploadFileRequest.Metadata>()
        val bytes = payloads.map { it.data.readBytes() }

        flowOf(service.uploadFile(UploadFileRequest(metadata.fileType, bytes))
            .asPayload())
    }
}