package io.timemates.backend.application.dependencies

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
import io.timemates.backend.services.authorization.GrpcAuthorizationsMapper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Duration.Companion.minutes

val AuthorizationsModule = module {
    singleOf(::TableAuthorizationsDataSource)
    single {
        CacheAuthorizationsDataSource(1000, 5.minutes)
    }
    singleOf(::DbAuthorizationsMapper)
    single<VerificationsRepository> {
        PostgresqlVerificationsRepository(TableVerificationsDataSource(get(), DbVerificationsMapper()), VerificationsMapper())
    }
    singleOf(::PostgresqlAuthorizationsRepository)
    singleOf(::AuthorizationsMapper)
    singleOf(::GetAuthorizationUseCase)
    singleOf(::GrpcAuthorizationsMapper)
    singleOf(::GetAuthorizationsUseCase)

    // Use cases
    singleOf(::AuthByEmailUseCase)
    singleOf(::ConfigureNewAccountUseCase)
    singleOf(::RemoveAccessTokenUseCase)
    singleOf(::RemoveAccessTokenUseCase)
    singleOf(::VerifyAuthorizationUseCase)
    singleOf(::GetAuthorizationUseCase)
}