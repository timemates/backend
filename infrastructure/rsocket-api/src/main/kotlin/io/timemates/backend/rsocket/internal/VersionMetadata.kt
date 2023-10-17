package io.timemates.backend.rsocket.internal

import com.y9vad9.rsocket.router.versioning.Version
import io.ktor.utils.io.core.*
import io.ktor.utils.io.core.internal.*
import io.ktor.utils.io.pool.*
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.core.MimeType
import io.rsocket.kotlin.core.WellKnownMimeType
import io.rsocket.kotlin.metadata.Metadata
import io.rsocket.kotlin.metadata.MetadataReader

@ExperimentalMetadataApi
class VersionMetadata(val version: Version) : Metadata {
    override val mimeType: MimeType get() = Reader.mimeType

    override fun BytePacketBuilder.writeSelf() {
        writeInt(version.major)
        writeInt(version.minor)
        writeInt(version.patch)
    }

    override fun close(): Unit = Unit

    companion object Reader : MetadataReader<VersionMetadata> {
        override val mimeType: MimeType get() = WellKnownMimeType.MessageRSocketRouting
        override fun ByteReadPacket.read(pool: ObjectPool<ChunkBuffer>): VersionMetadata {
            return VersionMetadata(Version(readInt(), readInt(), readInt()))
        }
    }
}
