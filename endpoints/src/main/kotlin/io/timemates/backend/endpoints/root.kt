package io.timemates.backend.endpoints

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.timemates.backend.application.plugins.AuthorizationPlugin
import io.timemates.backend.application.repositories.*
import io.timemates.backend.endpoints.auth.authRoot
import io.timemates.backend.endpoints.files.filesRoot
import io.timemates.backend.endpoints.timer.timersRoot
import io.timemates.backend.endpoints.users.usersRoot
import io.timemates.backend.endpoints.types.value.internal
import io.timemates.backend.endpoints.types.value.serializable
import io.timemates.backend.integrations.cache.storage.UsersCacheDataSource
import io.timemates.backend.integrations.inmemory.repositories.InMemorySchedulesRepository
import io.timemates.backend.integrations.inmemory.repositories.InMemorySessionsRepository
import io.timemates.backend.integrations.local.files.LocalFilesRepository
import io.timemates.backend.integrations.postgresql.repositories.datasource.*
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.*
import io.timemates.backend.repositories.integration.SMTPEmailsRepository
import io.timemates.backend.usecases.auth.*
import io.timemates.backend.usecases.files.GetFileUseCase
import io.timemates.backend.usecases.timers.*
import io.timemates.backend.usecases.timers.members.GetMembersInSessionUseCase
import io.timemates.backend.usecases.timers.members.GetMembersUseCase
import io.timemates.backend.usecases.timers.members.KickTimerUserUseCase
import io.timemates.backend.usecases.timers.members.invites.CreateInviteUseCase
import io.timemates.backend.usecases.timers.members.invites.GetInvitesUseCase
import io.timemates.backend.usecases.timers.members.invites.JoinByInviteUseCase
import io.timemates.backend.usecases.timers.members.invites.RemoveInviteUseCase
import io.timemates.backend.usecases.timers.sessions.JoinSessionUseCase
import io.timemates.backend.usecases.timers.sessions.LeaveSessionUseCase
import io.timemates.backend.usecases.users.EditUserUseCase
import io.timemates.backend.usecases.users.GetUsersUseCase
import io.timemates.backend.usecases.users.SetAvatarUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.exposed.sql.Database
import java.security.SecureRandom
import java.time.ZoneId
import java.util.*
import kotlin.io.path.Path
import kotlin.properties.Delegates

fun Routing.setupRoutes(
    authRepository: AuthorizationsRepository,
    timerInvitesRepository: DbTimerInvitesRepository,
    timersRepository: DbTimersRepository,
    usersRepository: DbUsersRepository,
    sessionsRepository: SessionsRepository,
    schedulesRepository: SchedulesRepository,
    filesRepository: FilesRepository,
    timerActivityRepository: TimerActivityRepository,
    verificationsRepository: VerificationsRepository,
    emailsRepository: EmailsRepository
) {
    val timeProvider =
        SystemCurrentTimeProvider(TimeZone.getTimeZone(ZoneId.of("Europe/Kiev")))
    val randomStringProvider = SecureRandomStringProvider(SecureRandom())

    val getUserIdByAccessTokenUseCase = GetUserIdByAccessTokenUseCase(authRepository, timeProvider)

    application.install(AuthorizationPlugin) {
        authorization = {
            (getUserIdByAccessTokenUseCase(it.internal())
                as? GetUserIdByAccessTokenUseCase.Result.Success)?.userId?.serializable()
        }
    }

    authRoot(
        RemoveAccessTokenUseCase(authRepository),
        RefreshTokenUseCase(
            randomStringProvider, authRepository, timeProvider
        ),
        AuthByEmailUseCase(
            emailsRepository, verificationsRepository, timeProvider, randomStringProvider
        ),
        VerifyAuthorizationUseCase(
            verificationsRepository,
            authRepository,
            randomStringProvider,
            usersRepository,
            timeProvider
        ),
        ConfigureNewAccountUseCase(
            usersRepository,
            authRepository,
            verificationsRepository,
            timeProvider,
            randomStringProvider
        )
    )

    timersRoot(
        CreateTimerUseCase(
            timersRepository,
            timeProvider
        ),
        GetTimersUseCase(timersRepository, sessionsRepository),
        GetTimerUseCase(timersRepository, sessionsRepository),
        RemoveTimerUseCase(timersRepository),
        SetTimerSettingsUseCase(timersRepository, sessionsRepository),
        StartTimerUseCase(timersRepository, timeProvider, sessionsRepository, timerActivityRepository),
        StopTimerUseCase(timersRepository, sessionsRepository, timerActivityRepository, timeProvider),
        CreateInviteUseCase(timerInvitesRepository, timersRepository, randomStringProvider, timeProvider),
        GetInvitesUseCase(timerInvitesRepository, timersRepository),
        JoinByInviteUseCase(timerInvitesRepository, timersRepository, timeProvider),
        RemoveInviteUseCase(timerInvitesRepository, timersRepository),
        JoinSessionUseCase(
            timersRepository,
            sessionsRepository,
            schedulesRepository,
            timeProvider,
            usersRepository
        ),
        LeaveSessionUseCase(sessionsRepository, schedulesRepository, usersRepository),
        ConfirmStartUseCase(timersRepository, sessionsRepository, timeProvider),
        LeaveTimerUseCase(timersRepository),
        KickTimerUserUseCase(timersRepository),
        GetMembersUseCase(timersRepository, usersRepository),
        GetMembersInSessionUseCase(timersRepository, sessionsRepository, usersRepository),
    )

    usersRoot(
        GetUsersUseCase(usersRepository),
        EditUserUseCase(usersRepository),
        SetAvatarUseCase(filesRepository, usersRepository)
    )

    filesRoot(GetFileUseCase(filesRepository))
}

fun Routing.setupRoutesWithDatabase(
    database: Database,
    rootFilesPath: String,
    smtpHost: String,
    smtpPort: Int,
    smtpUsername: String,
    smtpPassword: String?,
    smtpSenderEmail: String
) {
    val authRepository =
        DbAuthorizationsRepository(
            AuthorizationsDataSource(
                database
            )
        )
    val timerInvitesRepository = DbTimerInvitesRepository(TimerInvitesDataSource(database))
    val timersRepository = DbTimersRepository(TimersDatabaseDataSource(database))
    val usersRepository = DbUsersRepository(
        DbUsersDatabaseDataSource(database),
        UsersCacheDataSource(500)
    )
    val schedulesRepository = InMemorySchedulesRepository(
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    )
    val sessionsRepository =
        InMemorySessionsRepository()

    val filesRepository = LocalFilesRepository(Path(rootFilesPath))
    val timerActivityRepository = DbTimerActivityRepository(DbTimerActivityDataSource(database))
    val verificationsRepository: VerificationsRepository by Delegates.notNull()
    val emailsRepository = SMTPEmailsRepository(
        smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSenderEmail
    )

    setupRoutes(
        authRepository,
        timerInvitesRepository,
        timersRepository,
        usersRepository,
        sessionsRepository,
        schedulesRepository,
        filesRepository,
        timerActivityRepository,
        verificationsRepository,
        emailsRepository
    )
}