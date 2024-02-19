package org.timemates.backend.timers.deps.repositories

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.adapters.UsersRepositoryAdapter
import org.timemates.backend.timers.domain.repositories.UsersRepository
import org.timemates.backend.users.domain.repositories.UsersRepository as UsersRepositoryDelegate

@Module
class AdaptersModule {
    @Factory
    fun usersRepositoryAdapter(delegate: UsersRepositoryDelegate): UsersRepository {
        return UsersRepositoryAdapter(delegate)
    }
}