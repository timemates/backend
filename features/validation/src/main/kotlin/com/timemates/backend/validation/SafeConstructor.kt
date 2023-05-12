package com.timemates.backend.validation

import com.timemates.backend.validation.exceptions.ValidationFailure

/**
 * Abstraction for factories that construct value objects.
 * Next pattern should be applied to the factories:
 * - Factory should be in companion object that does only one thing – constructing.
 * - Validation information (like sizes or patterns) should be on the top of
 * the factories in order to better readability.
 * - After validation information comes [create] and, if needed, constants
 * with messages below the method.
 *
 * This is abstract class on purpose: to support clearness and readability of
 * value objects.
 *
 * **You should always implement a constructor for value objects, even if
 * there is no actual restrictions on given type. It will help to minimize
 * possible changes and support existing code style rules.**
 */
public abstract class SafeConstructor<Type, WrappedType> {
    /**
     * Method to construct valid instance of [Type].
     *
     * In addition, this function can transform input if needed (for example,
     * to remove multiple spaces or something like that, but it shouldn't
     * make something really different on user input to avoid misunderstanding
     * from user).
     *
     * @see ValidationFailureHandler
     * @return [Type] or fails in [ValidationFailureHandler].
     */
    context(ValidationFailureHandler)
    public abstract fun create(
        value: WrappedType
    ): Type
}

/**
 * Constructs a [T] from [W] with validation check in unsafe way. You should
 * use it only if it comes from trusted source (like database or from generator)
 *
 * @see [ValidationFailureHandler]
 * @see [SafeConstructor.create]
 * @throws [com.timemates.backend.validation.exceptions.ValidationFailure] if validation failed.
 */
@Throws(ValidationFailure::class)
public fun <T, W> SafeConstructor<T, W>.createOrThrow(value: W): T {
    return with(ValidationFailureHandler.ALWAYS_THROWS) {
        create(value)
    }
}

/**
 * Instantiates [Result] from [createOrThrow]. Catches only
 * [ValidationFailure].
 *
 * @see createOrThrow
 */
public fun <T, W> SafeConstructor<T, W>.createAsResult(value: W): Result<T> {
    return try {
        Result.success(createOrThrow(value))
    } catch (failure: ValidationFailure) {
        Result.failure(failure)
    }
}
