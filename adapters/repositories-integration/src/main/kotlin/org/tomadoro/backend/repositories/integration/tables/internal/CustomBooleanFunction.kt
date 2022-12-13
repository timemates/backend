package org.tomadoro.backend.repositories.integration.tables.internal

import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.QueryBuilder

internal fun distinct(vararg expressions: Expression<*>): CustomFunction<Boolean?> = CustomBooleanFunction(
    functionName = "DISTINCT ON",
    postfix = " TRUE",
    params = *expressions
)

internal fun CustomBooleanFunction(
    functionName: String, postfix: String = "", vararg params: Expression<*>
): CustomFunction<Boolean?> =
    object : CustomFunction<Boolean?>(functionName, BooleanColumnType(), *params) {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            super.toQueryBuilder(queryBuilder)
            if (postfix.isNotEmpty()) {
                queryBuilder.append(postfix)
            }
        }
    }