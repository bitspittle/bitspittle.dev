package dev.bitspittle.site.external.firebase

import dev.bitspittle.site.external.firebase.app.FirebaseApp
import dev.bitspittle.site.external.firebase.app.initializeApp

object Firebase {
    fun initialize(config: FirebaseConfig): FirebaseApp = initializeApp(config.toJson())
}