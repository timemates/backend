package io.timemates.backend.repositories.datasources.types

import io.timemates.backend.types.value.Count
import io.timemates.backend.types.value.Milliseconds
import io.timemates.backend.repositories.TimersRepository
import kotlinx.serialization.Serializable

@Serializable
class NewSettings(
    val workTime: Long? = null,
    val restTime: Long? = null,
    val bigRestTime: Long? = null,
    val bigRestEnabled: Boolean? = null,
    val bigRestPer: Int? = null,
    val isEveryoneCanPause: Boolean? = null,
    val isConfirmationRequired: Boolean? = null,
    val isNotesEnabled: Boolean? = null
)

fun TimersRepository.NewSettings.internal() = NewSettings(
    workTime?.long,
    restTime?.long,
    bigRestTime?.long,
    bigRestEnabled,
    bigRestPer?.int,
    isEveryoneCanPause,
    isConfirmationRequired,
    isNotesEnabled
)

fun NewSettings.external() = TimersRepository.NewSettings(
    workTime?.let { Milliseconds(it) },
    restTime?.let { Milliseconds(it) },
    bigRestTime?.let { Milliseconds(it) },
    bigRestEnabled,
    bigRestPer?.let { Count(it) },
    isEveryoneCanPause,
    isConfirmationRequired
)