package io.timemates.backend.application.dependencies

val AppModule = listOf(
    DatabaseModule,
    UsersModule,
    TimersModule,
    FilesModule,
    AuthorizationsModule,
    CommonModule,
    ServicesModule,
)