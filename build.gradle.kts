plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }
}
