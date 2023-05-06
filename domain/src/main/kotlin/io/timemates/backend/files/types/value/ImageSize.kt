package io.timemates.backend.files.types.value

import com.timemates.backend.validation.FailureMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationFailureHandler

@JvmInline
value class ImageSize private constructor(val size: Int) {
    companion object : SafeConstructor<ImageSize, Int>() {

        context(ValidationFailureHandler)
        override fun create(value: Int): ImageSize {
            return when {
                value <= 0 -> ImageSize(value)
                else -> onFail(VALUE_SHOULD_BE_POSITIVE)
            }
        }

        private val VALUE_SHOULD_BE_POSITIVE =
            FailureMessage("Image size cannot be negative or zero.")
    }
}