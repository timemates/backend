package io.timemates.backend.application.dependencies

val AppModule = listOf(
    DatabaseModule,
    CommonModule,
    UsersModule,
    TimersModule,
    TimerSessionsModule,
    FilesModule,
    AuthorizationsModule,
    ServicesModule,
)