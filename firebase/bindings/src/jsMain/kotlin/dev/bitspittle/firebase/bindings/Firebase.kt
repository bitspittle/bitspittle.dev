package dev.bitspittle.firebase.bindings

import dev.bitspittle.firebase.bindings.app.FirebaseApp
import dev.bitspittle.firebase.bindings.app.FirebaseOptions
import dev.bitspittle.firebase.bindings.database.*
import kotlinx.coroutines.await
import kotlin.js.Json
import kotlin.js.json

/**
 * A type-safe API for accessing top level methods in the various Firebase modules.
 */
object Firebase {
    object App {
        fun FirebaseOptions(
            apiKey: String,
            authDomain: String,
            databaseURL: String,
            projectId: String,
            storageBucket: String,
            messagingSenderId: String,
            appId: String,
            measurementId: String? = null,
        ) = object : FirebaseOptions {
            override val apiKey = apiKey
            override val authDomain = authDomain
            override val databaseURL = databaseURL
            override val projectId = projectId
            override val storageBucket = storageBucket
            override val messagingSenderId = messagingSenderId
            override val appId = appId
            override val measurementId = measurementId
        }

        fun initializeApp(options: FirebaseOptions, name: String? = null) =
            dev.bitspittle.firebase.bindings.app.initializeApp(
                json(
                    "apiKey" to options.apiKey,
                    "authDomain" to options.authDomain,
                    "databaseURL" to options.databaseURL,
                    "projectId" to options.projectId,
                    "storageBucket" to options.storageBucket,
                    "messagingSenderId" to options.messagingSenderId,
                    "appId" to options.appId,
                    "measurementId" to options.measurementId,
                ),
                name
            )
    }

    object Database {
        fun TransactionOptions(applyLocally: Boolean = true) = object : TransactionOptions {
            override val applyLocally = applyLocally
        }

        fun child(parent: DatabaseReference, path: String) =
            dev.bitspittle.firebase.bindings.database.child(parent, path)

        suspend fun get(query: Query) =
            dev.bitspittle.firebase.bindings.database.get(query).await()

        fun getDatabase(app: FirebaseApp, url: String? = null) =
            dev.bitspittle.firebase.bindings.database.getDatabase(app, url)

        fun increment(delta: Number) = dev.bitspittle.firebase.bindings.database.increment(delta)

        fun limitToFirst(limit: Number) = dev.bitspittle.firebase.bindings.database.limitToFirst(limit)

        fun limitToLast(limit: Number) = dev.bitspittle.firebase.bindings.database.limitToLast(limit)

        fun orderByChild(path: String) = dev.bitspittle.firebase.bindings.database.orderByChild(path)

        fun push(ref: DatabaseReference) =
            dev.bitspittle.firebase.bindings.database.push(ref)

        fun query(query: Query, vararg constraints: QueryConstraint) =
            dev.bitspittle.firebase.bindings.database.query(query, *constraints)

        fun ref(db: dev.bitspittle.firebase.bindings.database.Database, path: String? = null) =
            dev.bitspittle.firebase.bindings.database.ref(db, path)

        suspend fun remove(ref: DatabaseReference) {
            dev.bitspittle.firebase.bindings.database.remove(ref).await()
        }

        suspend fun runTransaction(
            ref: DatabaseReference,
            transactionUpdate: (currentData: Any) -> Any?,
            options: TransactionOptions? = null,
        ) =
            dev.bitspittle.firebase.bindings.database.runTransaction(
                ref,
                transactionUpdate = { transactionUpdate(it as Any) },
                options
            ).await()

        suspend fun set(ref: DatabaseReference, value: Any?) {
            dev.bitspittle.firebase.bindings.database.set(ref, value).await()
        }

        suspend fun update(ref: DatabaseReference, values: Json) {
            dev.bitspittle.firebase.bindings.database.update(ref, values).await()
        }
    }
}

fun FirebaseApp.getDatabase(url: String? = null) = Firebase.Database.getDatabase(this, url)

fun Database.ref(path: String? = null) = Firebase.Database.ref(this, path)

fun DatabaseReference.child(path: String) = Firebase.Database.child(this, path)
fun DatabaseReference.push() = Firebase.Database.push(this)
suspend fun DatabaseReference.remove() = Firebase.Database.remove(this)
suspend fun DatabaseReference.runTransation(transactionUpdate: (currentData: Any) -> Any?, transactionOptions: TransactionOptions? = null) =
    Firebase.Database.runTransaction(this, transactionUpdate, transactionOptions)
suspend fun DatabaseReference.set(value: Any) = Firebase.Database.set(this, value)
suspend fun DatabaseReference.update(values: Json) = Firebase.Database.update(this, values)

fun Query.query(vararg constraints: QueryConstraint) = Firebase.Database.query(this, *constraints)
