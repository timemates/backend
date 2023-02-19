package io.timemates.backend.users.repositories

import java.io.InputStream
import java.io.OutputStream

interface ImageCompressorRepository {
    /**
     * Compresses the given image in [inputStream] to given [width] and [height].
     *
     * @return [InputStream] with compressed image or `null`.
     */
    suspend fun compress(
        inputStream: InputStream,
        width: Int,
        height: Int,
    ): OutputStream?
}

suspend fun ImageCompressorRepository.compress(
    inputStream: InputStream, size: Int,
): OutputStream? {
    return compress(inputStream, size, size)
}

suspend fun ImageCompressorRepository.compressOrThrow(
    inputStream: InputStream, size: Int,
): OutputStream {
    return compress(inputStream, size, size) ?: error("Compressing of size $size is failed")
}