package org.timemates.backend.foundation.validation.test

import org.timemates.backend.validation.SafeConstructor
import org.timemates.backend.validation.createOr
import kotlin.test.asserter

public fun <T, W> SafeConstructor<T, W>.createOrAssert(value: W): T {
    return createOr(value) { cause ->
        asserter.fail("Unable to instantiate value, provided value: `$value`.", cause)
    }
}