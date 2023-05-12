package com.timemates.backend.validation

import com.timemates.backend.validation.exceptions.ValidationFailure

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
         * @throws [ValidationFailure] if validation failed.
         */
        public val ALWAYS_THROWS: ValidationFailureHandler = ValidationFailureHandler {
            failure -> throw ValidationFailure(failure.string)
        }
    }
}

/**
 * Creates a handler for validation errors.
 *
 * @param handler for processing invalid data.
 * @param block to work in.
 */
public fun validation(
    handler: (FailureMessage) -> Nothing,
    block: context(ValidationFailureHandler) () -> Unit
) {
    val instance = ValidationFailureHandler(handler)
    return block(instance)
}