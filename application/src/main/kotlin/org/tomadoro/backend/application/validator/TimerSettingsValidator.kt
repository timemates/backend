package org.tomadoro.backend.application.validator

//import io.ktor.server.plugins.requestvalidation.*
//
//fun RequestValidationConfig.timerSettingsValidator() {
//    validate<TimerSettings> {
//        when {
//            it.workTime.long in 60000L..28800000L ->
//                ValidationResult.Invalid("Work time should be in range of 1 minute and until 8 hours.")
//
//            it.bigRestTime.long in 60000L..240000 ->
//                ValidationResult.Invalid("Big rest time should be in range of 1 minute and until 4 hours.")
//
//            it.restTime.long in 60000L..3600000L ->
//                ValidationResult.Invalid("Rest time should be in range of 1 minute and until 1 hour.")
//
//            it.bigRestPer.int in 2..12 ->
//                ValidationResult.Invalid("bigRestPer should be in range of 2 and 12.")
//
//            else -> ValidationResult.Valid
//        }
//    }
//}