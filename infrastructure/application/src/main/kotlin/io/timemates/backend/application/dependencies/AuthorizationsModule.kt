package io.timemates.backend.application.dependencies

import io.timemates.backend.authorization.repositories.AuthorizationsRepository
import io.timemates.backend.authorization.repositories.VerificationsRepository
import io.timemates.backend.authorization.usecases.*
import io.timemates.backend.data.authorization.PostgresqlAuthorizationsRepository
import io.timemates.backend.data.authorization.PostgresqlVerificationsRepository
import io.timemates.backend.data.authorization.cache.CacheAuthorizationsDataSource
import io.timemates.backend.data.authorization.db.TableAuthorizationsDataSource
import io.timemates.backend.data.authorization.db.TableVerificationsDataSource
import io.timemates.backend.data.authorization.db.mapper.DbAuthorizationsMapper
import io.timemates.backend.data.authorization.db.mapper.DbVerificationsMapper
import io.timemates.backend.data.authorization.mapper.AuthorizationsMapper
import io.timemates.backend.data.authorization.mapper.VerificationsMapper
import org.koin.dsl.module
import kotlin.time.Duration.Companion.minutes

val AuthorizationsModule = module {
    single {
        TableAuthorizationsDataSource(
            database = get(),
            mapper = get(),
            json = get(),
        )
    }
    single {
        CacheAuthorizationsDataSource(1000, 5.minutes)
    }
    single {
        DbAuthorizationsMapper()
    }
    single<VerificationsRepository> {
        PostgresqlVerificationsRepository(TableVerificationsDataSource(get(), DbVerificationsMapper()), VerificationsMapper())
    }
    single<AuthorizationsRepository> {
        PostgresqlAuthorizationsRepository(
            tableAuthorizationsDataSource = get(),
            cacheAuthorizations = get(),
            mapper = get(),
        )
    }
    single {
        AuthorizationsMapper()
    }
    single {
        GetAuthorizationUseCase(
            authorizationsRepository = get(),
            timerProvider = get(),
        )
    }

    // Use cases
    single {
        AuthByEmailUseCase(
            emails = get(),
            verifications = get(),
            timeProvider = get(),
            randomProvider = get(),
        )
    }
    single {
        ConfigureNewAccountUseCase(
            users = get(),
            authorizations = get(),
            verifications = get(),
            timeProvider = get(),
            randomProvider = get()
        )
    }
    single {
        RefreshTokenUseCase(
            randomProvider = get(),
            authorizations = get(),
            time = get(),
        )
    }
    single {
        RemoveAccessTokenUseCase(tokens = get())
    }
    single {
        VerifyAuthorizationUseCase(
            verifications = get(),
            authorizations = get(),
            randomProvider = get(),
            users = get(),
            timeProvider = get(),
        )
    }
    single {
        GetAuthorizationUseCase(
            authorizationsRepository = get(),
            timerProvider = get(),
        )
    }
}