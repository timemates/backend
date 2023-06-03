# Welcome to TimeMates Backend!

TimeMates Backend is a project aimed at helping people manage their time better. It is a server-side application that
provides
the necessary endpoints for client-side applications to connect to and manage time.

## Project Structure

This project is divided into the following modules:

- **domain**: This module contains the domain models and business logic. We use Domain Driven Design (DDD) principles to
  organize the code
  in this module. Each domain is divided into repositories, types, and usecases packages. types contains models that are
  used in use-cases and
  repositories, while usecases contains all the application logic that uses the domain models. repositories contains
  interfaces that define how
  the use-cases interact with the persistence layer.
- **data**: This module is responsible for managing the data layer of the application. The datasources package contains
  implementations
  for retrieving data from external sources (e.g., a database). The root package for specific domain repositories
  contains the repository
  implementation details with the used datasources.
- **infrastucture**: Layer that decides how data and logic interacts with world. For example, GRPC endpoints.
- **features**: This module provides reusable libraries for authorization with scopes, validation, time, random, etc.
  All of these features include
  interfaces and implementation. We're trying to divide everything that we can to **features** module to make our code
  more clear.

### domain and DDD

The goal of Domain Driven Design (DDD) is to help us create software that is closely aligned with the business domain.
In the context of this project, we organize our code into domains that correspond to the business problems we are trying
to solve.
Each domain has its own `repositories`, `types`, and `usecases` packages, which allow us to separate the different
concerns of the application.

- **types**: This package contains domain models that represent the data that we need to store and manipulate in the
  application.
  These models are typically simple data classes that contain the information that we need to represent a specific
  business concept.
- **repositories**: This package contains interfaces that define how we interact with the persistence layer of the
  application.
  These interfaces define the basic CRUD (Create, Read, Update, Delete) operations that we need to perform on our domain
  models.
  The interfaces are usually implemented in the data module.
- **usecases**: This package contains the application logic that uses the domain models. Use-cases are the entry point
  for clients that want to use
  our application. They orchestrate the interactions between the domain models and the repositories to provide the
  functionality that the clients need.

### Data Layer

The data module is responsible for managing the persistence layer of the application. It contains implementations for
retrieving data
from external sources (e.g., a database). The `datasources` package contains implementations for retrieving data from
external sources.
The root package for specific domain repositories contains the repository implementation details with the used
datasources.

For example:

- `io.timemates.backend.data.users`
    - `datasources`
        - `PostgresqlUsersDataSource`
        - `CachedUsersDataSource`
    - `UsersRepository`
    - `UserEntitiesMapper` (all mappers used in users repository)

## Testing

In this project, we use unit testing to ensure that our code works as expected. Unit tests are automated tests that are
designed to test a
single unit of code in isolation from the rest of the application.

### Data Layer

When it comes to testing the data layer, we want to ensure that the data we are storing and retrieving is correct and
that the repository functions
as expected. We also want to test the mappers to make sure that data is being converted correctly between the domain
layer and the data layer.

To accomplish this, we use a testing framework like JUnit (kotlin.test) and a mocking library like MockK. MockK allows
us to create mock objects that
behave like the real ones but with controlled behavior. We can use these mock objects to test our repository functions
without having to use real data,
which can be costly and time-consuming.

For example, suppose we have a `UsersRepository` that is responsible for storing and retrieving user information from a
database. To test the repository
functions, we can create a mock database object that behaves like a real database but only returns the data that we
specify. This way, we can test the
repository functions with different data scenarios and ensure that they behave as expected.

#### Mappers

To test the mappers, we can create mock domain and data layer objects and check whether the mappers correctly convert
data from one form to another.
For example, if we have a UserMapper that converts UserEntity objects from the data layer to User objects from the
domain layer, we can create a mock
UserEntity object and ensure that the UserMapper correctly converts it to a User object.

Here are some resources for learning more about MockK for newbies:

- Official MockK documentation: https://mockk.io/
- A tutorial on using MockK for testing Kotlin code: https://www.baeldung.com/kotlin/mockk

When it comes to testing, we want to make sure that our tests are well-organized and easy to understand. We can use a
testing framework
like JUnit (kotlin.test) or to group our tests by functionality, and we can use naming conventions to make it clear what
each test is testing.
We can also use code comments to explain what each test (using backticks with human-readable names) is doing and why it
is important.

Always check for examples!

## Code Review

Code review is a critical part of our development process. All code changes must go through code review before being
merged into the main branch.
This ensures that our codebase is maintainable, extensible, and adheres to our best practices.

When submitting a pull request for code review, it's important to include a description of the changes you made and why
you made them. This can help
reviewers understand the context and purpose of your changes, which can make the review process smoother.

Code reviews should be constructive and respectful. Reviewers should focus on providing feedback that helps improve the
code, rather than criticizing
or attacking the developer. Developers should be open to feedback and willing to make changes to improve the quality of
their code.

### Best Practices

Here are some additional best practices that we follow:

- **Write clean and readable code**: Your code should be easy to read and understand. Use clear and descriptive variable
  and function names, break
  up long functions, and include comments where necessary (even if it seems obvious for you).
- **Test your code**: Make sure that your code is thoroughly tested and that tests are passing before submitting it for
  review.
- **Follow coding standards and conventions**: Follow established coding standards and conventions for the project and
  [kotlin](https://kotlinlang.org/docs/coding-conventions.html).
- **Use version control**: Always use version control and commit your changes frequently. Make sure to write clear and
  descriptive commit messages.
- **Document your code**: Document your code to make it easier for others to understand how it works and how to use it.
- **Continuously learn and improve**: Keep learning new skills, technologies, and best practices to improve your work
  and contribute to the success of the team.

## Conclusion

Hope this helps! Good luck and welcome aboard!
