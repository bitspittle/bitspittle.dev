@file:JsModule("firebase/app")
@file:JsNonModule
package dev.bitspittle.firebase.bindings.app

import kotlin.js.Json

// https://firebase.google.com/docs/reference/js/app.firebaseapp.md
external interface FirebaseApp {
    val name: String
    val options: FirebaseOptions
}

// https://firebase.google.com/docs/reference/js/app.firebaseoptions
external interface FirebaseOptions {
    val apiKey: String
    val authDomain: String
    val databaseURL: String
    val projectId: String
    val storageBucket: String
    val messagingSenderId: String
    val appId: String
    val measurementId: String?
}

// https://firebase.google.com/docs/reference/js/app.md#initializeapp_2
internal external fun initializeApp(options: Json, name: String?): FirebaseApp
