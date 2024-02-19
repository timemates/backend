package org.timemates.backend.validation.reflection

import org.timemates.backend.validation.SafeConstructor

@Suppress("UnusedReceiverParameter")
public inline fun <reified T> SafeConstructor<T, *>.wrapperTypeName(): Lazy<String> =
    lazy { T::class.simpleName!! }