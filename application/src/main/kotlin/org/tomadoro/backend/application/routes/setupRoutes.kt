package org.tomadoro.backend.application.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.exposed.sql.Database
import org.tomadoro.backend.application.plugins.AuthorizationPlugin
import org.tomadoro.backend.application.routes.auth.authRoot
import org.tomadoro.backend.application.routes.timer.timersRoot
import org.tomadoro.backend.application.routes.users.usersRoot
import org.tomadoro.backend.codes.integration.SecureCodeProvider
import org.tomadoro.backend.google.auth.GoogleClient
import org.tomadoro.backend.providers.SecureAccessTokenProvider
import org.tomadoro.backend.providers.SecureRefreshTokenProvider
import org.tomadoro.backend.providers.SystemCurrentTimeProvider
import org.tomadoro.backend.repositories.integration.*
import org.tomadoro.backend.repositories.integration.datasource.AuthorizationsDataSource
import org.tomadoro.backend.repositories.integration.datasource.TimerInvitesDataSource
import org.tomadoro.backend.repositories.integration.datasource.TimersDatabaseDataSource
import org.tomadoro.backend.repositories.integration.datasource.UsersDatabaseDataSource
import org.tomadoro.backend.usecases.auth.AuthViaGoogleUseCase
import org.tomadoro.backend.usecases.auth.GetUserIdByAccessTokenUseCase
import org.tomadoro.backend.usecases.auth.RefreshTokenUseCase
import org.tomadoro.backend.usecases.auth.RemoveAccessTokenUseCase
import org.tomadoro.backend.usecases.timers.*
import org.tomadoro.backend.usecases.timers.invites.CreateInviteUseCase
import org.tomadoro.backend.usecases.timers.invites.GetInvitesUseCase
import org.tomadoro.backend.usecases.timers.invites.JoinByInviteUseCase
import org.tomadoro.backend.usecases.timers.invites.RemoveInviteUseCase
import org.tomadoro.backend.usecases.users.EditUserUseCase
import org.tomadoro.backend.usecases.users.GetUsersUseCase
import org.tomadoro.backend.usecases.users.SetAvatarUseCase
import java.time.ZoneId
import java.util.*
import kotlin.io.path.Path

fun Routing.setupRoutes(
    authRepository: AuthorizationsRepository,
    linkedSocialsRepository: LinkedSocialsRepository,
    timerInvitesRepository: TimerInvitesRepository,
    timersRepository: TimersRepository,
    usersRepository: UsersRepository,
    sessionsRepository: SessionsRepository,
    schedulesRepository: SchedulesRepository,
    filesRepository: FilesRepository,
    googleClient: GoogleClient
) {
    val timeProvider =
        SystemCurrentTimeProvider(TimeZone.getTimeZone(ZoneId.of("Europe/Kiev")))
    val accessTokenProvider = SecureAccessTokenProvider
    val refreshTokenProvider = SecureRefreshTokenProvider
    val codesProvider = SecureCodeProvider

    application.install(AuthorizationPlugin) {
        authorize = GetUserIdByAccessTokenUseCase(authRepository, timeProvider)
    }

    authRoot(
        AuthViaGoogleUseCase(
            linkedSocialsRepository,
            usersRepository,
            accessTokenProvider,
            authRepository,
            timeProvider,
            refreshTokenProvider,
            googleClient
        ),
        RemoveAccessTokenUseCase(authRepository),
        RefreshTokenUseCase(
            accessTokenProvider, authRepository, timeProvider
        )
    )

    timersRoot(
        CreateTimerUseCase(
            timersRepository,
            timeProvider
        ),
        GetTimersUseCase(timersRepository),
        GetTimerUseCase(timersRepository),
        RemoveTimerUseCase(timersRepository),
        SetTimerSettingsUseCase(timersRepository, sessionsRepository),
        StartTimerUseCase(timersRepository, timeProvider, sessionsRepository),
        StopTimerUseCase(timersRepository, sessionsRepository),
        CreateInviteUseCase(timerInvitesRepository, timersRepository, codesProvider),
        GetInvitesUseCase(timerInvitesRepository, timersRepository),
        JoinByInviteUseCase(timerInvitesRepository, timersRepository),
        RemoveInviteUseCase(timerInvitesRepository, timersRepository),
        JoinSessionUseCase(timersRepository, sessionsRepository, schedulesRepository, timeProvider),
        LeaveSessionUseCase(sessionsRepository, schedulesRepository),
        ConfirmStartUseCase(timersRepository, sessionsRepository, timeProvider)
    )

    usersRoot(
        GetUsersUseCase(usersRepository),
        EditUserUseCase(usersRepository),
        SetAvatarUseCase(filesRepository, usersRepository)
    )

    ok()
}

fun Routing.setupRoutesWithDatabase(
    database: Database,
    googleClient: GoogleClient,
    rootFilesPath: String
) {
    val authRepository =
        AuthorizationsRepository(AuthorizationsDataSource(database))
    val linkedSocialsRepository = LinkedSocialsRepository(database)
    val timerInvitesRepository = TimerInvitesRepository(TimerInvitesDataSource(database))
    val timersRepository = TimersRepository(TimersDatabaseDataSource(database))
    val usersRepository = UsersRepository(UsersDatabaseDataSource(database))
    val schedulesRepository = SchedulesRepository(
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    )
    val sessionsRepository =
        SessionsRepository(
            timersRepository,
            CoroutineScope(Dispatchers.Default + SupervisorJob())
        )

    val filesRepository = FilesRepository(Path(rootFilesPath))

    setupRoutes(
        authRepository,
        linkedSocialsRepository,
        timerInvitesRepository,
        timersRepository,
        usersRepository,
        sessionsRepository,
        schedulesRepository,
        filesRepository,
        googleClient
    )
}