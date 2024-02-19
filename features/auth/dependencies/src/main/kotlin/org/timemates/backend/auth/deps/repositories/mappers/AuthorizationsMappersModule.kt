package org.timemates.backend.auth.deps.repositories.mappers

import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import org.timemates.backend.auth.data.db.mapper.DbAuthorizationsMapper
import org.timemates.backend.auth.data.db.mapper.DbVerificationsMapper
import org.timemates.backend.auth.data.mapper.AuthorizationsMapper
import org.timemates.backend.auth.data.mapper.VerificationsMapper

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

    @Singleton
    fun verificationsMapper(): VerificationsMapper {
        return VerificationsMapper()
    }

    @Singleton
    fun authMapper(): AuthorizationsMapper {
        return AuthorizationsMapper()
    }
}