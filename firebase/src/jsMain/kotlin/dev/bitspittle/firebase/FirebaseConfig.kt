package dev.bitspittle.site.external.firebase

import kotlin.js.json

// https://firebase.google.com/docs/reference/firebase-management/rest/v1beta1/projects.webApps/getConfig
class FirebaseConfig(
    val apiKey: String,
    val appId: String,
    val authDomain: String? = null,
    val databaseURL: String? = null,
    val locationId: String? = null,
    val storageBucket: String? = null,
    val projectId: String? = null,
    val messagingSenderId: String? = null,
    val measurementId: String? = null,
)

fun FirebaseConfig.toJson() = json(
    "apiKey" to apiKey,
    "appId" to appId,
    "authDomain" to (authDomain ?: undefined),
    "databaseURL" to (databaseURL ?: undefined),
    "locationId" to (locationId ?: undefined),
    "storageBucket" to (storageBucket ?: undefined),
    "projectId" to (projectId ?: undefined),
    "messagingSenderId" to (messagingSenderId ?: undefined),
    "measurementId" to (measurementId ?: undefined),
)

