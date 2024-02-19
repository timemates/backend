# Authorization Library

The 'authorization' library provides a set of utilities to handle user authorization and access control
within an application. It offers an explicit and structured approach to manage authorization requirements
in use cases, promoting secure and organized code.

## Purpose

The main purpose of this library is to enable explicit handling of authorization within use cases, without 
the need for boilerplate code that is often repeated. It addresses the common challenge of ensuring that use
cases are executed only when the user has the necessary authorization and promotes the use of well-defined 
authorization scopes.

## AuthorizedContext

The `AuthorizedContext` interface represents the authorization context of the current user. It provides 
access to the user's authorization information, such as their ID. Use cases can require a specific 
authorization context to enforce the necessary authorization for execution.

## Scope

The `Scope` interface represents an authorization scope, denoting different levels of permissions or access rights. Scopes help define granular authorization requirements and allow for fine-grained access control within the application.

## Usage

To utilize the 'authorization' library, follow these steps:

1. Identify the use cases that require authorization. These use cases typically involve operations that access sensitive data or perform critical actions.

2. Annotate the corresponding use case methods with the `context(AuthorizedContext<Scope>)` annotation. This annotation indicates that the use case requires a specific authorization context defined by the associated scope.

3. Ensure that the authorization context is provided when executing the annotated use cases. This can be achieved using the `authorizationProvider` function, which creates an `AuthorizedContext` with the desired scope.

4. Use the provided authorization context within the annotated use case methods to enforce the necessary authorization checks.

By following these steps, you can explicitly handle authorization requirements in a structured and organized manner, enhancing the security and maintainability of your application.

## Example

Here's an example illustrating the usage of the 'authorization' library:

```kotlin
// Use case declaration
class EditUserUseCase(private val usersRepository: UsersRepository) {
    context(AuthorizedContext<UsersScope.Write>)
    suspend fun execute(patch: User.Patch): Result {
        usersRepository.edit(userId, patch)
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
    }
}

// Usage in gRPC
override suspend fun setUser(request: EditUserRequestOuterClass.EditUserRequest): Empty = provideAuthorizationContext {
    val patch = mapper.toGrpcUserPatch(request)

    when (editUserUseCase.execute(patch)) {
        EditUserUseCase.Result.Success -> Empty.getDefaultInstance()
    }
}
```

In this example, the `EditUserUseCase` requires the `UsersScope.Write` authorization context, indicating that the use
case
can only be executed by users with write access to user-related operations. The `provideAuthorizationContext`
[(source)](/../../infrastructure/grpc-api/src/main/kotlin/org.timemates.backend/services/authorization/context/provideAuthorizationContext.kt)
function ensures that the authorization context is available when executing the use case.

By using the 'authorization' library, you can have clear and explicit authorization requirements in your use cases,
enhancing the security and control of your application.