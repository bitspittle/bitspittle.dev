subprojects {
    repositories {
        mavenLocal {
            content {
                includeModule("dev.bitspittle", "firebase-bindings")
            }
        }
        mavenCentral()
        google()
        maven("https://us-central1-maven.pkg.dev/varabyte-repos/public")
    }
}
