package io.timemates.backend.validation

import io.timemates.backend.validation.exceptions.InternalValidationFailure

/**
 * Scope that handles validation failures and propagates
 * it to the top of the hierarchy.
 *
 * **Example:**
 * ```kotlin
 * context(ValidationFailureHandler)
 * fun editUserName(userName: String): Result {
 *     // Finishes the execution on failure
 *     val name: UserName = UserName.create(userName)
 *
 *     val result = saveUserNameUseCase.execute(name)
 *     // ...
 * }
 *
 * fun someRouting() = patch(..) {
 *    withValidation({ return@patch call.respond(HttpStatus.BadRequest, it.string) }) {
 *       editUserName(call.queryParameters["name])
 *    }
 * }
 * ```
 */
public fun interface ValidationFailureHandler {
    /**
     * Stops the execution and propagates failure to the top of the hierarchy.
     *
     * @param message – readable message for request output.
     */
    public fun onFail(message: FailureMessage): Nothing

    public companion object {
        /**
         * Scope that should be used if validation should always throw exception.
         *
         * @throws [InternalValidationFailure] if validation failed.
         */
        internal val THROWS_INTERNAL: ValidationFailureHandler = ValidationFailureHandler { failure ->
            throw InternalValidationFailure(failure.string)
        }
    }
}