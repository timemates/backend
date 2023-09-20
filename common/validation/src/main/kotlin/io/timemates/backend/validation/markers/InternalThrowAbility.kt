package io.timemates.backend.validation.markers

/**
 * Interface-marker for functions that intended to be used only in context where's
 * safe to throw just exceptions, instead of handling it.
 *
 * Shouldn't be implemented directly to throw exceptions where it's needed, but
 * should create another interface-marker for that.
 *
 * @see [io.timemates.backend.common.markers.UseCase]
 */
public interface InternalThrowAbility