package com.timemates.backend.validation.reflection

import com.timemates.backend.validation.SafeConstructor

/**
 * Gets the name of wrapper for [SafeConstructor.displayName].
 */
public inline fun <reified T> SafeConstructor<T, *>.wrapperTypeName(): Lazy<String> =
    lazy { T::class.simpleName!! }