package io.timemates.backend.rsocket.internal

import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import io.ktor.utils.io.pool.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.core.MimeType
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.metadata.Metadata
import io.rsocket.kotlin.metadata.MetadataReader

@ExperimentalMetadataApi
fun AuthorizationMetadata(vararg tags: String): AuthorizationMetadata = AuthorizationMetadata(tags.toList())

@ExperimentalMetadataApi
class AuthorizationMetadata(val tags: List<String>) : Metadata {
    init {
        tags.forEach {
            require(it.length in 1..255) { "Tag length must be in range 1..255 but was '${it.length}'" }
        }
    }

    override val mimeType: MimeType get() = Reader.mimeType

    override fun BytePacketBuilder.writeSelf() {
        tags.forEach {
            val bytes = it.encodeToByteArray()
            writeByte(bytes.size.toByte())
            writeFully(bytes)
        }
    }

    override fun close(): Unit = Unit

    companion object Reader : MetadataReader<AuthorizationMetadata> {
        override val mimeType: MimeType get() = WellKnownMimeType.MessageRSocketRouting
        override fun ByteReadPacket.read(pool: ObjectPool<ChunkBuffer>): AuthorizationMetadata {
            val list = mutableListOf<String>()
            while (isNotEmpty) {
                val length = readByte().toInt() and 0xFF
                list.add(readTextExactBytes(length))
            }
            return AuthorizationMetadata(list.toList())
        }
    }
}
