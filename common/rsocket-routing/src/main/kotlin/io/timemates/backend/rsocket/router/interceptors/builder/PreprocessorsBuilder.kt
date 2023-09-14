package io.timemates.backend.rsocket.router.interceptors.builder

import io.timemates.backend.rsocket.router.annotations.ExperimentalRouterApi
import io.timemates.backend.rsocket.router.interceptors.Preprocessor

@ExperimentalRouterApi
public class PreprocessorsBuilder internal constructor() {
    private val preprocessors = mutableListOf<Preprocessor<*, *>>()

    public fun forCoroutineContext(preprocessor: Preprocessor.CoroutineContext) {
        preprocessors += preprocessor
    }

    public fun forModification(preprocessor: Preprocessor.Modifier) {
        preprocessors += preprocessor
    }

    internal fun build(): List<Preprocessor<*, *>> = preprocessors.toList()
}