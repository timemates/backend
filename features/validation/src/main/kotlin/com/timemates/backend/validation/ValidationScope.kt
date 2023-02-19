package com.timemates.backend.validation

/**
 * Scope that handles validation failures and propagates
 * it to the top of the hierarchy.
 *
 * **Example:**
 * ```kotlin
 * context(ValidScope)
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
public fun interface ValidationScope {
    /**
     * Stops the execution and propagates failure to the top of the hierarchy.
     *
     * @param readableMessage – readable message for request output.
     */
    public fun fail(readableMessage: ReadableMessage): Nothing
}

/**
 * Creates a handler for validation errors.
 *
 * @param handler for processing invalid data.
 * @param block to work in.
 */
public fun withValidation(
    handler: (ReadableMessage) -> Nothing,
    block: context(ValidationScope) () -> Unit
) {
    val instance = ValidationScope(handler)
    return block(instance)
}