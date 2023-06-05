package io.timemates.backend.application.dependencies

import io.timemates.backend.data.users.PostgresqlUsersRepository
import io.timemates.backend.data.users.UserEntitiesMapper
import io.timemates.backend.data.users.datasource.CachedUsersDataSource
import io.timemates.backend.data.users.datasource.PostgresqlUsersDataSource
import io.timemates.backend.users.usecases.EditUserUseCase
import io.timemates.backend.users.usecases.GetUsersUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val UsersModule = module {
    singleOf(::PostgresqlUsersDataSource)
    single {
        CachedUsersDataSource(100)
    }
    singleOf(::UserEntitiesMapper)
    singleOf(::PostgresqlUsersRepository)

    // Use cases
    singleOf(::EditUserUseCase)
    singleOf(::GetUsersUseCase)
}