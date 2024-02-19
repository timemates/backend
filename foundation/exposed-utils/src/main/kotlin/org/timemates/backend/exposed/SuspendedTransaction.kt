package org.timemates.backend.exposed

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.CoroutineContext

/**
 * Base pool of threads that are used for database connection.
 */
val Dispatchers.DB by lazy { Dispatchers.IO.limitedParallelism(60) }

/**
 * A suspend function that provides context for performing database transactions in
 * a suspended manner.
 * @param database The database on which the transaction is to be executed.
 * @param block The block of code that will be executed as part of the transaction.
 * @return [ReturnType] of the query.
 */
suspend inline fun <ReturnType> suspendedTransaction(
    database: Database,
    coroutineContext: CoroutineContext = Dispatchers.DB,
    noinline block: Transaction.() -> ReturnType,
): ReturnType = newSuspendedTransaction(
    context = coroutineContext,
    db = database,
    statement = block,
)