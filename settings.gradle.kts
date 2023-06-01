pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password =
                    "sk.eyJ1Ijoic2FsYXphcmJyb2NrIiwiYSI6ImNrd2F0dmU5cDBmemsycG13eW1tcXUycjEifQ.cWYnBOKVlQ6_3TLVKn-z-g"
            }
        }
        maven {
            url = uri("https://storage.googleapis.com/snap-kit-build/maven")
        }
    }
}
rootProject.name = "Cheers"

include(":app")
include(":ads")
include(":common")
include(":auth")

// Core
include(":core:protobuf")
include(":core:ui")
include(":core:util")
include(":core:model")

// Feature
include(":feature:search")
include(":feature:chat")
include(":feature:map")

// Data
include(":data:user")
include(":data:post")
include(":data:note")

project(":common").projectDir = File(settingsDir, "../common")
project(":ads").projectDir = File(settingsDir, "../ads")
project(":auth").projectDir = File(settingsDir, "../auth")
