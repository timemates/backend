package io.timemates.backend.auth.deps.repositories.mappers

import io.timemates.backend.auth.data.db.mapper.DbAuthorizationsMapper
import io.timemates.backend.auth.data.db.mapper.DbVerificationsMapper
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class AuthorizationsMappersModule {
    @Singleton
    fun dbAuthMapper(): DbAuthorizationsMapper {
        return DbAuthorizationsMapper()
    }

    @Singleton
    fun dbVerificationsMapper(): DbVerificationsMapper {
        return DbVerificationsMapper()
    }
}