package org.timemates.backend.auth.deps

import org.koin.core.annotation.Module
import org.timemates.backend.auth.deps.repositories.AuthorizationsRepositoriesModule
import org.timemates.backend.auth.deps.repositories.cache.CacheAuthorizationsModule
import org.timemates.backend.auth.deps.repositories.database.DatabaseAuthorizationModule
import org.timemates.backend.auth.deps.repositories.mailer.MailerModule
import org.timemates.backend.auth.deps.repositories.mappers.AuthorizationsMappersModule
import org.timemates.backend.auth.deps.usecases.*

@Module(
    includes = [
        // Data-related modules
        CacheAuthorizationsModule::class,
        DatabaseAuthorizationModule::class,
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