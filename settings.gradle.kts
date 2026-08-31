pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT) // Kotlin/JS dynamically adds repositories

    repositories {
        mavenLocal {
            content {
                // For firebase-bindings, which gets built locally only at this point
                includeGroup("dev.bitspittle")
            }
        }
        mavenCentral()
        google()
    }
}


rootProject.name = "bitspittledev"

include(":site")
