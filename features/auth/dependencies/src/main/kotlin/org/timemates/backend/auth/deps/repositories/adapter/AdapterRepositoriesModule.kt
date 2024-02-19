package org.timemates.backend.auth.deps.repositories.adapter

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.timemates.backend.auth.adapters.UsersRepositoryDelegateAdapter
import org.timemates.backend.auth.domain.repositories.UsersRepository
import org.timemates.backend.users.domain.repositories.UsersRepository as UsersRepositoryAdapter

@Module
class AdapterRepositoriesModule {
    @Factory
    fun usersRepository(delegate: UsersRepositoryAdapter): UsersRepository = UsersRepositoryDelegateAdapter(delegate)
}