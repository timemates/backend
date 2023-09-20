package io.timemates.backend.rsocket.test

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
                    RoutingMetadata("authorizations"),
                    AuthorizationMetadata("..."),
                )
            )
            data(ByteReadPacket.Empty)
        }

        val output = preprocessor.intercept(currentCoroutineContext(), payload)

        val element = output[AuthorizableRouteContext]
        assertNotNull(element)
        assertEquals(element.route, "authorizations")
        assertEquals(element.accessHash, "...")
    }
}