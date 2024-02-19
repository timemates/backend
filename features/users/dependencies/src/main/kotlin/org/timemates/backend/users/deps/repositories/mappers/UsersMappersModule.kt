package org.timemates.backend.users.deps.repositories.mappers

import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton
import org.timemates.backend.users.data.UserEntitiesMapper

@Module
class UsersMappersModule {
    @Singleton
    fun userEntitiesMapper(): UserEntitiesMapper = UserEntitiesMapper()
}