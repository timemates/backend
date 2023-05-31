package com.timemates.backend.validation

/**
 * Class that contains human-readable message of
 * validation failure.
 */
@JvmInline
public value class FailureMessage(public val string: String) {
    public companion object {
        /**
         * Creates a `FailureMessage` object with a size constraint failure message.
         *
         * @param size The size range constraint for the creation failure.
         * @return The `FailureMessage` object with the specified size constraint failure message.
         */
        context (SafeConstructor<*, *>)
        public fun ofSize(size: IntRange): FailureMessage {
            return FailureMessage(
                "Constraint failure: $displayName size must be in range of $size"
            )
        }

        /**
         * Creates a [FailureMessage] with a constraint failure message based on the provided size.
         * @param size The expected size that caused the constraint failure.
         * @return A [FailureMessage] object with the constraint failure message.
         */
        context (SafeConstructor<*, *>)
        public fun ofSize(size: Int): FailureMessage {
            return FailureMessage(
                "Constraint failure: $displayName size must be exactly $size"
            )
        }

        context (SafeConstructor<*, *>)
        public fun ofMin(size: Int): FailureMessage {
            return FailureMessage(
                "Constraint failure: $displayName minimal value is the $size"
            )
        }

        context (SafeConstructor<*, *>)
        public fun ofNegative(): FailureMessage {
            return FailureMessage("Constraint failure: $displayName cannot be negative")
        }

        /**
         * Creates a [FailureMessage] with a constraint failure message for a blank value.
         * @return A [FailureMessage] object with the constraint failure message.
         */
        context (SafeConstructor<*, *>)
        public fun ofBlank(): FailureMessage {
            return FailureMessage("Constraint failure: $displayName provided value is empty")
        }

        /**
         * Creates a `FailureMessage` object with a pattern constraint failure message.
         *
         * @param regex The regular expression pattern constraint for the creation failure.
         * @return The `FailureMessage` object with the specified pattern constraint failure message.
         */
        context (SafeConstructor<*, *>)
        public fun ofPattern(regex: Regex): FailureMessage {
            return FailureMessage(
                "Constraint failure: $displayName value should match $regex"
            )
        }


    }

    /**
     * Returns [string]
     */
    override fun toString(): String {
        return string
    }
}