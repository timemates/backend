package io.timemates.backend.files.types.value

import com.timemates.backend.validation.ReadableMessage
import com.timemates.backend.validation.SafeConstructor
import com.timemates.backend.validation.ValidationScope

@JvmInline
value class ImageSize private constructor(val size: Int) {
    companion object : SafeConstructor<ImageSize, Int>() {

        context(ValidationScope)
        override fun create(value: Int): ImageSize {
            return when {
                value <= 0 -> ImageSize(value)
                else -> fail(VALUE_SHOULD_BE_POSITIVE)
            }
        }

        private val VALUE_SHOULD_BE_POSITIVE =
            ReadableMessage("Image size cannot be negative or zero.")
    }
}