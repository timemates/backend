package io.timemates.backend.validation

import io.timemates.backend.validation.exceptions.InternalValidationFailure
import io.timemates.backend.validation.markers.InternalThrowAbility

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
     * Name of the class what is validated. Used to display for API
     * responses.
     */
    public abstract val displayName: String

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
        value: WrappedType,
    ): Type
}

/**
 * Constructs a [T] from [W] with validation check in unsafe way. You should
 * use it only if it comes from trusted source (like database or from generator)
 *
 * @see [ValidationFailureHandler]
 * @see [SafeConstructor.create]
 * @throws [com.timemates.backend.validation.exceptions.InternalValidationFailure] if validation failed.
 */
context (InternalThrowAbility)
@Throws(InternalValidationFailure::class)
public fun <T, W> SafeConstructor<T, W>.createOrThrowInternally(value: W): T {
    return with(io.timemates.backend.validation.ValidationFailureHandler.THROWS_INTERNAL) {
        create(value)
    }
}
