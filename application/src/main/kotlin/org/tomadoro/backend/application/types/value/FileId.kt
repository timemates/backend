package org.tomadoro.backend.application.types.value

import kotlinx.serialization.Serializable
import org.tomadoro.backend.repositories.FilesRepository

@Serializable
@JvmInline
value class FileId(val string: String)

fun FilesRepository.FileId.serializable() = FileId(string)
fun FileId.internal() = FilesRepository.FileId(string)