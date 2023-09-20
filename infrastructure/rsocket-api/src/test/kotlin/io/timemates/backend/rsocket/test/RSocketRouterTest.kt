package io.timemates.backend.rsocket.test

import com.y9vad9.rsocket.router.annotations.ExperimentalInterceptorsApi
import com.y9vad9.rsocket.router.test.routeAtOrAssert
import io.mockk.mockk
import io.timemates.backend.rsocket.interceptors.AuthorizableRoutePreprocessor
import io.timemates.backend.rsocket.timeMatesRouter
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains

@OptIn(ExperimentalInterceptorsApi::class)
class RSocketRouterTest {
    private val requestPreprocessor = mockk<AuthorizableRoutePreprocessor>()

    private val router = timeMatesRouter(
        auth = mockk(),
        users = mockk(),
        timers = mockk(),
        timerMembers = mockk(),
        timerInvites = mockk(),
        timerSessions = mockk(),
        files = mockk(),
        requestInterceptor = requestPreprocessor,
    )


    @Test
    fun `AuthorizableRouterPreprocessor should be registered`() {
        assertContains(router.preprocessors, requestPreprocessor)
    }

    @Test
    fun `router should register all services`(): Unit = runBlocking {
        router.routeAtOrAssert("authorizations")
        router.routeAtOrAssert("users")
        router.routeAtOrAssert("timers")
        router.routeAtOrAssert("files")
    }
}