package io.timemates.backend.exposed

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

fun <T : Table> T.update(
    where: Op<Boolean>,
    limit: Int? = null,
    statement: T.(UpdateBuilder<Int>) -> Unit,
): Int {
    return update(where = { where }, limit = limit) {
        statement(it)
    }
}

fun <T : Table> T.insert(
    statement: T.(UpdateBuilder<Int>) -> Unit,
): Int {
    return insert {
        statement(it)
    }
}

/**
 * Inserts or updates a row in the [T] table.
 *
 * @param condition based on given condition, it will either update or
 * insert a row (same conditions applies for update and [condition] check).
 * @param statement that whether sets or inserts a row.
 *
 * You should always be aware of situation, when there's no row, and you're missing
 * column in query.
 */
fun <T : Table> T.upsert(
    condition: Op<Boolean>,
    statement: T.(UpdateBuilder<Int>, exists: Boolean) -> Unit,
): Int {
    val exists = !select(condition).empty()

    return if (exists) {
        update(condition) { statement(it, true) }
    } else {
        insert { statement(it, false) }
    }
}