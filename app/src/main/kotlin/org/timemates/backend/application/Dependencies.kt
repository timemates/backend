package org.timemates.backend.application

import com.timemates.backend.time.SystemTimeProvider
import com.timemates.backend.time.TimeProvider
import com.timemates.random.RandomProvider
import com.timemates.random.SecureRandomProvider
import org.jetbrains.exposed.sql.Database
import org.koin.core.Koin
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.ksp.generated.module
import org.timemates.api.rsocket.RSProtoServicesModule
import org.timemates.backend.auth.deps.AuthorizationsModule
import org.timemates.backend.auth.deps.repositories.mailer.MailerModule
import org.timemates.backend.timers.deps.TimersModule
import org.timemates.backend.users.deps.UsersModule
import kotlin.time.Duration

fun initDeps(
    databaseUser: String,
    databasePassword: String,
    databaseUrl: String,
    mailerImplementation: MailerModule.MailerImplementation,
    timersCacheSize: Long,
    usersCacheSize: Long,
    authMaxCacheEntities: Long,
    authMaxAliveTime: Duration,
    isDebug: Boolean,
): Koin {
    return koinApplication {
        val databaseModule = module {
            single<Database> {
                return@single Database.connect(
                    url = databaseUrl,
                    user = databaseUser,
                    password = databasePassword,
                )
            }
        }

        val mailerModule = module {
            single { mailerImplementation }
        }

        val applicationModule = module {
            single(StringQualifier("application.isDebug")) { isDebug }
        }

        val cacheModule = module {
            single(StringQualifier("timers.cache.size")) { timersCacheSize }
            single(StringQualifier("users.cache.size")) { usersCacheSize }
            single(StringQualifier("auth.cache.size")) { authMaxCacheEntities }
            single(StringQualifier("auth.cache.alive")) { authMaxAliveTime }
        }

        val foundationModule = module {
            single<TimeProvider> { SystemTimeProvider() }
            single<RandomProvider> { SecureRandomProvider() }
        }

        modules(
            databaseModule,
            mailerModule,
            applicationModule,
            cacheModule,
            foundationModule,
            UsersModule().module,
            AuthorizationsModule().module,
            TimersModule().module,
            RSProtoServicesModule().module,
        )
    }.koin
}