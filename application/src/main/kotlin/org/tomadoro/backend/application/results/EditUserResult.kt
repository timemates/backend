package org.tomadoro.backend.application.results

import kotlinx.serialization.Serializable

@Serializable
sealed interface EditUserResult {
    @Serializable
    object Success : EditUserResult
}