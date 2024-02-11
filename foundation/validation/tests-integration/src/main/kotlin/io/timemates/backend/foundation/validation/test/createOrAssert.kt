package io.timemates.backend.foundation.validation.test

import io.timemates.backend.validation.SafeConstructor
import io.timemates.backend.validation.createOr
import kotlin.test.asserter

public fun <T, W> SafeConstructor<T, W>.createOrAssert(value: W): T {
    return createOr(value) { cause ->
        asserter.fail("Unable to instantiate value, provided value: `$value`.", cause)
    }
}