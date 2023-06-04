package io.timemates.backend.application.dependencies

import io.timemates.backend.data.users.PostgresqlUsersRepository
import io.timemates.backend.data.users.UserEntitiesMapper
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.users.repositories.UsersRepository
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase
import org.koin.dsl.module

val UsersModule = module {
    single {
        PostgresqlUsersDataSource(get())
    }
    single {
        CachedUsersDataSource(100)
    }
    single {
        UserEntitiesMapper()
    }
    single<UsersRepository> {
        PostgresqlUsersRepository(
            postgresqlUsers = get(),
            cachedUsers = get(),
            mapper = get(),
        )
    }

    // Use cases
    single {
        EditUserUseCase(usersRepository = get())
    }
    single {
        GetUsersUseCase(usersRepository = get())
    }
}