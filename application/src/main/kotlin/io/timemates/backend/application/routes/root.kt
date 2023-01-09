package io.timemates.backend.application.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.exposed.sql.Database
import io.timemates.backend.application.plugins.AuthorizationPlugin
import io.timemates.backend.application.routes.auth.authRoot
import io.timemates.backend.application.routes.files.filesRoot
import io.timemates.backend.application.routes.timer.timersRoot
import io.timemates.backend.application.routes.users.usersRoot
import io.timemates.backend.application.types.value.internal
import io.timemates.backend.application.types.value.serializable
import io.timemates.backend.providers.SecureRandomStringProvider
import io.timemates.backend.providers.SystemCurrentTimeProvider
import io.timemates.backend.repositories.AuthorizationsRepository
import io.timemates.backend.repositories.FilesRepository
import io.timemates.backend.repositories.TimerActivityRepository
import io.timemates.backend.integrations.postgresql.repositories.*
import io.timemates.backend.integrations.postgresql.repositories.datasource.*
import io.timemates.backend.usecases.auth.GetUserIdByAccessTokenUseCase
import io.timemates.backend.usecases.auth.RefreshTokenUseCase
import io.timemates.backend.usecases.auth.RemoveAccessTokenUseCase
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
import java.security.SecureRandom
import java.time.ZoneId
import java.util.*
import kotlin.io.path.Path

fun Routing.setupRoutes(
    authRepository: AuthorizationsRepository,
    timerInvitesRepository: TimerInvitesRepository,
    timersRepository: TimersRepository,
    usersRepository: DbUsersRepository,
    sessionsRepository: SessionsRepository,
    schedulesRepository: SchedulesRepository,
    filesRepository: FilesRepository,
    timerActivityRepository: TimerActivityRepository
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
        CreateInviteUseCase(timerInvitesRepository, timersRepository, randomStringProvider),
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
    rootFilesPath: String
) {
    val authRepository =
        io.timemates.backend.integrations.postgresql.repositories.DbAuthorizationsRepository(
            AuthorizationsDataSource(
                database
            )
        )
    val timerInvitesRepository = TimerInvitesRepository(TimerInvitesDataSource(database))
    val timersRepository = TimersRepository(TimersDatabaseDataSource(database))
    val usersRepository = DbUsersRepository(DbUsersDatabaseDataSource(database))
    val schedulesRepository = SchedulesRepository(
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    )
    val sessionsRepository =
        SessionsRepository()

    val filesRepository = LocalFilesRepository(Path(rootFilesPath))
    val timerActivityRepository = TimerActivityRepository(DbTimerActivityDataSource(database))

    setupRoutes(
        authRepository,
        timerInvitesRepository,
        timersRepository,
        usersRepository,
        sessionsRepository,
        schedulesRepository,
        filesRepository,
        timerActivityRepository
    )
}