package io.timemates.backend.application.dependencies

import io.timemates.backend.application.dependencies.configuration.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module

val DatabaseModule = module {
    single {
        val config = get<DatabaseConfig>()

        Database.connect(
            url = config.url,
            user = config.user,
            password = config.password ?: "",
        )
    }
}