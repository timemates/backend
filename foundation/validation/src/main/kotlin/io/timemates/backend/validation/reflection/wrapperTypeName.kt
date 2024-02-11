package io.timemates.backend.validation.reflection

import io.timemates.backend.validation.SafeConstructor

@Suppress("UnusedReceiverParameter")
public inline fun <reified T> SafeConstructor<T, *>.wrapperTypeName(): Lazy<String> =
    lazy { T::class.simpleName!! }