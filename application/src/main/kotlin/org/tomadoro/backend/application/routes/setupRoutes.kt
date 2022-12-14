package org.tomadoro.backend.application.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.exposed.sql.Database
import org.tomadoro.backend.application.plugins.AuthorizationPlugin
import org.tomadoro.backend.application.routes.auth.authRoot
import org.tomadoro.backend.application.routes.files.filesRoot
import org.tomadoro.backend.application.routes.timer.timersRoot
import org.tomadoro.backend.application.routes.users.usersRoot
import org.tomadoro.backend.codes.integration.SecureCodeProvider
import org.tomadoro.backend.google.auth.GoogleClient
import org.tomadoro.backend.providers.SecureAccessTokenProvider
import org.tomadoro.backend.providers.SecureRefreshTokenProvider
import org.tomadoro.backend.providers.SystemCurrentTimeProvider
import org.tomadoro.backend.repositories.TimerActivityRepository
import org.tomadoro.backend.repositories.integration.*
import org.tomadoro.backend.repositories.integration.datasource.*
import org.tomadoro.backend.usecases.auth.AuthViaGoogleUseCase
import org.tomadoro.backend.usecases.auth.GetUserIdByAccessTokenUseCase
import org.tomadoro.backend.usecases.auth.RefreshTokenUseCase
import org.tomadoro.backend.usecases.auth.RemoveAccessTokenUseCase
import org.tomadoro.backend.usecases.files.GetFileUseCase
import org.tomadoro.backend.usecases.timers.*
import org.tomadoro.backend.usecases.timers.members.GetMembersInSessionUseCase
import org.tomadoro.backend.usecases.timers.members.GetMembersUseCase
import org.tomadoro.backend.usecases.timers.members.KickTimerUserUseCase
import org.tomadoro.backend.usecases.timers.members.invites.CreateInviteUseCase
import org.tomadoro.backend.usecases.timers.members.invites.GetInvitesUseCase
import org.tomadoro.backend.usecases.timers.members.invites.JoinByInviteUseCase
import org.tomadoro.backend.usecases.timers.members.invites.RemoveInviteUseCase
import org.tomadoro.backend.usecases.timers.notes.AddNoteUseCase
import org.tomadoro.backend.usecases.timers.notes.GetLatestUserNotesUseCase
import org.tomadoro.backend.usecases.timers.notes.GetNotesUseCase
import org.tomadoro.backend.usecases.timers.sessions.JoinSessionUseCase
import org.tomadoro.backend.usecases.timers.sessions.LeaveSessionUseCase
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
    notesRepository: NotesRepository,
    timerActivityRepository: TimerActivityRepository,
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
        GetTimersUseCase(timersRepository, sessionsRepository),
        GetTimerUseCase(timersRepository, sessionsRepository),
        RemoveTimerUseCase(timersRepository),
        SetTimerSettingsUseCase(timersRepository, sessionsRepository),
        StartTimerUseCase(timersRepository, timeProvider, sessionsRepository, timerActivityRepository),
        StopTimerUseCase(timersRepository, sessionsRepository, timerActivityRepository, timeProvider),
        CreateInviteUseCase(timerInvitesRepository, timersRepository, codesProvider),
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
        AddNoteUseCase(notesRepository, timersRepository, timeProvider, sessionsRepository, timerActivityRepository),
        GetNotesUseCase(notesRepository, timersRepository),
        GetMembersUseCase(timersRepository, usersRepository),
        GetMembersInSessionUseCase(timersRepository, sessionsRepository, usersRepository),
        GetLatestUserNotesUseCase(timersRepository, notesRepository)
    )

    usersRoot(
        GetUsersUseCase(usersRepository),
        EditUserUseCase(usersRepository),
        SetAvatarUseCase(filesRepository, usersRepository)
    )

    filesRoot(GetFileUseCase(filesRepository))

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
    val usersRepository = UsersRepository(DbUsersDatabaseDataSource(database))
    val schedulesRepository = SchedulesRepository(
        CoroutineScope(Dispatchers.Default + SupervisorJob())
    )
    val sessionsRepository =
        SessionsRepository()

    val filesRepository = FilesRepository(Path(rootFilesPath))
    val timerActivityRepository = TimerActivityRepository(DbTimerActivityDataSource(database))
    val notesRepository = NotesRepository(DbTimerNotesDatasource(database), DbTimerNotesViewsDataSource(database))

    setupRoutes(
        authRepository,
        linkedSocialsRepository,
        timerInvitesRepository,
        timersRepository,
        usersRepository,
        sessionsRepository,
        schedulesRepository,
        filesRepository,
        notesRepository,
        timerActivityRepository,
        googleClient
    )
}