package dev.bitspittle.site.external.firebase.app

import dev.bitspittle.site.external.firebase.FirebaseConfig
import dev.bitspittle.site.external.firebase.toJson
import kotlin.js.Json

// https://firebase.google.com/docs/reference/js/v8/firebase.app.App#properties
external class FirebaseApp {
    val name: String
}

@JsModule("firebase/app")
@JsNonModule()
internal external fun initializeApp(config: Json): FirebaseApp
