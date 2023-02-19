package io.timemates.backend.endpoints.types.value

import kotlinx.serialization.Serializable
import io.timemates.backend.repositories.FilesRepository

@Serializable
@JvmInline
value class FileId(val string: String)

fun FilesRepository.FileId.serializable() = FileId(string)
fun FileId.internal() = FilesRepository.FileId(string)