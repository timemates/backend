# Core Module Documentation

The `core` module is responsible for encapsulating the core domain logic of the application.

## Purpose

The purpose of the `core` module is to define and model the essential concepts of the application's domain. It contains the key components that represent the core domain entities, value objects, exceptions, use cases, and repositories.

## Components

### Types (entities)

Entities represent the fundamental objects in the domain. They encapsulate behavior and hold state relevant to the business domain.

### Value Objects

Value objects are immutable objects that encapsulate a set of attributes or properties. They represent concepts within the domain and are characterized by their values rather than their identities.

#### Validation Approach

The `core` module follows an explicit validation approach without relying on exceptions. The goal is to ensure intentional validation and explicit handling of validation failures.

##### SafeConstructor

The `SafeConstructor` class is a key component for constructing valid instances of value objects with validation checks and explicit failure handling.

- Factory Pattern: Each value object has a corresponding `SafeConstructor` implementation acting as a factory for creating valid instances.

- Validation Information: `SafeConstructor` includes validation information for readability and understanding of the validation requirements.

- Explicit Validation: The `create` method within `SafeConstructor` performs explicit validation, returning a valid instance or handling the failure explicitly.

### Example: EmailAddress Value Object

```kotlin
value class EmailAddress private constructor(val string: String) {
    companion object : SafeConstructor<EmailAddress, String>() {
        override val displayName: String = "Email Address"
        private val SIZE = 3..200
        private val EMAIL_PATTERN = Regex("<email-regex-pattern>")

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

### Use Cases

Use cases represent the application-specific business logic or operations that can be performed within the domain. They encapsulate the steps required to achieve a specific goal or fulfill a business requirement.

### Repositories

Repositories define contracts or interfaces for accessing and persisting domain entities. They provide an abstraction layer that decouples the domain logic from the underlying data storage or persistence mechanism.
