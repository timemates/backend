package io.timemates.backend.auth.deps

import io.timemates.backend.auth.data.cache.CacheAuthorizationsDataSource
import io.timemates.backend.auth.deps.repositories.AuthorizationsRepositoriesModule
import io.timemates.backend.auth.deps.repositories.database.AuthorizationDatabaseModule
import io.timemates.backend.auth.deps.repositories.mailer.MailerModule
import io.timemates.backend.auth.deps.repositories.mappers.AuthorizationsMappersModule
import io.timemates.backend.auth.deps.usecases.*
import org.koin.core.annotation.Module

@Module(
    includes = [
        // Data-related modules
        CacheAuthorizationsDataSource::class,
        AuthorizationDatabaseModule::class,
        MailerModule::class,
        AuthorizationsMappersModule::class,
        AuthorizationsRepositoriesModule::class,

        // UseCases
        AuthByEmailUseCaseModule::class,
        ConfigureNewAccountUseCaseModule::class,
        GetAuthorizationsUseCaseModule::class,
        GetAuthorizationUseCaseModule::class,
        GetUserIdByAccessTokenUseCaseModule::class,
        RefreshAccessTokenUseCaseModule::class,
        RemoveAccessTokenUseCaseModule::class,
        VerifyAuthorizationUseCaseModule::class,
    ],
)
class AuthorizationsModule