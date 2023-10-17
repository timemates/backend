package io.timemates.backend.rsocket.features.files

import com.y9vad9.rsocket.router.builders.RoutingBuilder

// TODO entire remake files service
fun RoutingBuilder.files(service: RSocketFilesService): Unit = route("files") {
//    requestStream("get") { payload ->
//        payload.decodingForFlow<GetFileRequest> {
//            service.getFile(it).map(GetFileRequest.Response::asPayload)
//        }
//    }
//
//    requestChannel("upload") { _, payloads ->
//        val metadata = payloads.first().decodeFromJson<UploadFileRequest.Metadata>()
//        val bytes = payloads.map { it.data.readBytes() }
//
//        flowOf(service.uploadFile(UploadFileRequest(metadata.fileType, bytes))
//            .asPayload())
//    }
}