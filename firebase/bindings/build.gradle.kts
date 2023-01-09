plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

group = "dev.bitspittle.firebase.bindings"
version = "0.1-SNAPSHOT"

kotlin {
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("firebase", libs.versions.firebase.get()))
                implementation(libs.kotlinx.coroutines)
             }
        }
    }
}

