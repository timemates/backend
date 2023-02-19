package io.timemates.backend.application.internal

@JvmInline
internal value class Arguments(private val array: Array<String>) {
    /**
     * @return [Boolean] whether the given [name] was presented in array of arguments.
     */
    fun isPresent(name: String): Boolean {
        return array.any { it.startsWith("-$name") }
    }

    /**
     * Returns value of the given argument with [name] or null.
     */
    fun getNamedOrNull(name: String): String? {
        val index = array.indexOfFirst { it.startsWith("-$name") }
            .takeIf { it >= 0 }

        return if (index != null) {
            array[index + 1]
        } else null
    }
}

internal fun Arguments.getNamedIntOrNull(name: String): Int? =
    getNamedOrNull(name)?.toInt()

internal fun Array<String>.asArguments(): Arguments = Arguments(this)