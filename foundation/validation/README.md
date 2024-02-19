# Validation Library

The Validation Library provides a robust and flexible validation approach to ensure data integrity and enforce business rules within your application.

## Key Features

- **Explicit Validation**: The library emphasizes explicit validation, avoiding exceptions and providing clear and predictable validation results. This approach promotes better error handling and reduces unexpected behavior.

- **Validation Failure Handling**: The library provides a comprehensive framework for handling validation failures. Developers can define custom failure handlers to handle validation errors in a way that aligns with the application's requirements.

- **Reusable Validation Components**: The library offers a set of reusable validation components, including validators, failure messages, and validation rules, allowing developers to easily define and apply validation logic across the application.

- **Value Object Validation**: The library supports validation for value objects through the use of the `SafeConstructor` class. This class provides a factory pattern for constructing valid instances of value objects while incorporating validation checks and explicit failure handling.

## SafeConstructor

The `SafeConstructor` class is a fundamental component of the Validation Library. It enables the creation of valid instances of value objects by applying validation rules and explicit failure handling.

### How it works

1. **Validation Information**: Each value object typically has a corresponding `SafeConstructor` implementation. This implementation contains validation information, such as size limits, patterns, or other constraints, for better readability and understanding of the value object's validation requirements.

2. **Explicit Validation**: The `create` method within the `SafeConstructor` implementation performs the validation process explicitly. It checks the provided value against the defined validation rules and returns a valid instance of the value object or handles the validation failure explicitly.

3. **Failure Handling**: In case of a validation failure, the `SafeConstructor` implementation employs a failure handler to handle the failure explicitly. The failure handler can be customized to provide error codes, error messages, or other appropriate actions based on the application's needs.

### Example

Let's illustrate the usage of `SafeConstructor` with an example of the `EmailAddress` value object:

```kotlin
value class EmailAddress private constructor(val string: String) {
    companion object : SafeConstructor<EmailAddress, String>() {
        override val displayName: String = "Email Address"
        private val SIZE = 3..200
        private val EMAIL_PATTERN = Regex("<email-regex-pattern>")

        context (ValidationFailureHandler)
        override fun create(value: String): EmailAddress {
            return when {
                value.isEmpty() -> onFail(FailureMessage.ofBlank())
                value.length !in SIZE -> onFail(FailureMessage.ofSize(SIZE))
                !EMAIL_PATTERN.matches(value) -> onFail(FailureMessage.ofPattern(EMAIL_PATTERN))
                else -> EmailAddress(value)
            }
        }
    }
}
```

In this example, the `EmailAddress` value object uses the `SafeConstructor` to ensure that the provided email address 
meets the defined validation rules. The `create` method performs explicit validation, checking for an empty 
value, size limits, and pattern matching. If any validation fails, the onFail function is used to handle the failure 
explicitly and provide an appropriate failure message.

By utilizing the Validation Library's SafeConstructor and following the explicit validation approach, TimeMates
application can enforce data integrity and promote reliable behavior throughout the domain (`:core` module).

## SafeConstructor Extensions

The `SafeConstructor` class provides several useful extensions that enhance its functionality and simplify common 
usage scenarios. Mostly, you should only use them, instead of `create`.

- `createOrThrow(value: W): T`: Constructs a `T` from a `W` value with validation checks in an unsafe way. This extension should be used only if the value comes from a trusted source, such as a database or a generator. It throws a `ValidationFailure` if the validation fails.

- `createAsResult(value: W): Result<T>`: Instantiates a `Result<T>` from `createOrThrow` by catching only `ValidationFailure`. This extension is useful for capturing validation failures as a result.

- `createOrStatus(value: W): T`: An extension function on `SafeConstructor` that creates an instance of `T` using the given `W` input. If the validation fails, it throws a `StatusException` with a `Status.FAILED_PRECONDITION` status code and a description of the validation failure message. This extension is designed for use in the context of gRPC.

- `createOrAssert(value: W): T`: A function primarily used for testing purposes, where a `ValidationFailureHandler` is needed. It creates an instance of `T` using the given `W` input, and if the validation fails, it throws an `AssertionError`. This extension is useful for testing scenarios.

These extensions provide additional convenience when working with `SafeConstructor` instances, allowing you to handle 
validation failures in different ways depending on the use case.
