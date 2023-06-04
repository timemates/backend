## Data Module

The `data` module is responsible for handling data access and interacting with external data sources in the application.

### Purpose

The primary purpose of the `data` module is to encapsulate the implementation of repositories. It provides an abstraction layer between the domain layer and the underlying data sources, such as databases, 
caches, or external services.

### Implementation Details

The `:data` module may include various implementations based on the specific data storage technologies and frameworks 
used in the application. These implementations could include database-specific code, caching mechanisms, or 
integrations with external services.

#### Example

Here's an example of a `UsersRepository` implementation within the `:data` module:

```kotlin
class PostgresqlUsersRepository(/* ... */) : UsersRepository {
    override suspend fun getUser(userId: UserId): User {
        return // ...
    }

    // ...
}
```