package org.tomadoro.backend.domain.value

@JvmInline
value class DateTime(val long: Long) {
    operator fun plus(long: Long): DateTime = DateTime(this.long + long)
    operator fun plus(dateTime: DateTime): DateTime = DateTime(this.long + dateTime.long)
    operator fun plus(ms: Milliseconds): DateTime = DateTime(this.long + ms.long)
    operator fun minus(dateTime: DateTime): Milliseconds = Milliseconds(long - dateTime.long)
}