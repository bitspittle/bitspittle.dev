@file:JsModule("firebase/database")
@file:JsNonModule
package dev.bitspittle.firebase.bindings.database

import dev.bitspittle.firebase.bindings.app.FirebaseApp
import kotlin.js.Json
import kotlin.js.Promise

// https://firebase.google.com/docs/reference/js/database.database
external class Database {
    val app: FirebaseApp
}

// https://firebase.google.com/docs/reference/js/database.databasereference
external interface DatabaseReference : Query {
    val key: String?
    val parent: DatabaseReference?
    val root: DatabaseReference
}

// https://firebase.google.com/docs/reference/js/database.datasnapshot
external class DataSnapshot {
    val key: String?
    val priority: dynamic // String | Number | null
    val ref: DatabaseReference
    val size: Number
}

// https://firebase.google.com/docs/reference/js/database.query
external interface Query {
    val ref: DatabaseReference

    fun isEqual(other: Query): Boolean
    fun toJSON(): Json
}

// https://firebase.google.com/docs/reference/js/database.query
abstract external class QueryConstraint {
    val type: String // https://firebase.google.com/docs/reference/js/database.md#queryconstrainttype
}

// https://firebase.google.com/docs/reference/js/database.transactionoptions
external interface TransactionOptions {
    val applyLocally: Boolean
}

// https://firebase.google.com/docs/reference/js/database.transactionresult
external class TransactionResult {
    val committed: Boolean
    val snapshot: DataSnapshot

    fun toJSON(): Json
}

// https://firebase.google.com/docs/reference/js/database.md#child
internal external fun child(parent: DatabaseReference, path: String): DatabaseReference

// https://firebase.google.com/docs/reference/js/database.md#getdatabase
internal external fun get(query: Query): Promise<DataSnapshot>

// https://firebase.google.com/docs/reference/js/database.md#getdatabase
internal external fun getDatabase(app: FirebaseApp, url: String?): Database

// https://firebase.google.com/docs/reference/js/database.md#increment
internal external fun increment(delta: Number): Json

// https://firebase.google.com/docs/reference/js/database.md#limittofirst
internal external fun limitToFirst(limit: Number): QueryConstraint

// https://firebase.google.com/docs/reference/js/database.md#limittofirst
internal external fun limitToLast(limit: Number): QueryConstraint

// https://firebase.google.com/docs/reference/js/database.md#orderbychild
internal external fun orderByChild(path: String): QueryConstraint

// https://firebase.google.com/docs/reference/js/database.md#push
internal external fun push(ref: DatabaseReference): DatabaseReference

// https://firebase.google.com/docs/reference/js/database.md#query
internal external fun query(query: Query, vararg queryConstraints: dynamic): DatabaseReference

// https://firebase.google.com/docs/reference/js/database.md#ref
internal external fun ref(db: Database, path: String?): DatabaseReference

// https://firebase.google.com/docs/reference/js/database.md#remove
internal external fun remove(ref: DatabaseReference): Promise<Unit>

// https://firebase.google.com/docs/reference/js/database#runtransaction
internal external fun runTransaction(
    ref: DatabaseReference,
    transactionUpdate: (currentData: dynamic) -> dynamic,
    options: TransactionOptions?
): Promise<TransactionResult>

// https://firebase.google.com/docs/reference/js/database.md#set
internal external fun set(ref: DatabaseReference, value: dynamic): Promise<Unit>

// https://firebase.google.com/docs/reference/js/database.md#update
internal external fun update(ref: DatabaseReference, values: Json): Promise<Unit>
