package io.timemates.backend.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.InputStream

private const val DEFAULT_BUFFER_SIZE = 4096

fun InputStream.asFlow(): Flow<ByteArray> = flow {
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var bytesRead = read(buffer)
    while (bytesRead >= 0) {
        emit(buffer.copyOf(bytesRead))
        bytesRead = read(buffer)
    }
}.flowOn(Dispatchers.IO).buffer(DEFAULT_BUFFER_SIZE)