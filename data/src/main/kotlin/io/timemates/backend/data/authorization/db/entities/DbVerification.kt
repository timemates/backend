package io.timemates.backend.data.authorization.db.entities

data class DbVerification(
    val verificationHash: String,
    val emailAddress: String,
    val code: String,
    val attempts: Int,
    val time: Long,
    val isConfirmed: Boolean,
)