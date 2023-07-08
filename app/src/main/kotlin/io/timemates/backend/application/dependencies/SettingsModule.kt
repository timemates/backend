package io.timemates.backend.application.dependencies

import io.timemates.backend.avatar.repositories.GravatarRepository as GravatarRepositoryContract
import io.timemates.backend.avatar.usecases.SetGravatarUseCase
import io.timemates.backend.data.settings.GravatarRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val SettingsModule = module {
    single<GravatarRepositoryContract> {
        GravatarRepository()
    }
    singleOf(::SetGravatarUseCase)
}