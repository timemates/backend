package io.timemates.backend.rsocket.test

import com.y9vad9.rsocket.router.versioning.Version
import io.ktor.utils.io.core.*
import io.mockk.mockk
import io.rsocket.kotlin.ExperimentalMetadataApi
import io.rsocket.kotlin.metadata.CompositeMetadata
import io.rsocket.kotlin.metadata.RoutingMetadata
import io.rsocket.kotlin.metadata.metadata
import io.rsocket.kotlin.payload.buildPayload
import io.timemates.backend.rsocket.interceptors.AuthorizableRouteContext
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutePreprocessor
import io.timemates.backend.rsocket.internal.AuthorizationMetadata
import io.timemates.backend.rsocket.internal.VersionMetadata
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthorizableRoutePreprocessorTest {
    private val preprocessor = AuthorizableRoutePreprocessor(mockk())

    @OptIn(ExperimentalMetadataApi::class)
    @Test
    fun `preprocessor parses metadata correctly`(): Unit = runBlocking {
        val payload = buildPayload {
            metadata(
                CompositeMetadata(
                    VersionMetadata(Version(1, 2, 0)),
                    RoutingMetadata("authorizations"),
                    AuthorizationMetadata("..."),
                )
            )
            data(ByteReadPacket.Empty)
        }

        val output = preprocessor.intercept(currentCoroutineContext(), payload)

        val element = output[AuthorizableRouteContext]
        assertNotNull(element)
        assertEquals(element.version, Version(1, 2, 0))
        assertEquals(element.route, "authorizations")
        assertEquals(element.accessHash, "...")
    }
}