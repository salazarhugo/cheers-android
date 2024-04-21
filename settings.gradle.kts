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

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":ads")
include(":common")
include(":auth")

// Core
include(":core:protobuf")
include(":core:ui")
include(":core:util")
include(":core:model")
include(":core:shared")

// Data
include(":data:user")
include(":data:post")
include(":data:note")
include(":data:auth")
include(":data:story")
include(":data:activity")
include(":data:party")
include(":data:friendship")
include(":data:billing")
include(":data:account")
include(":data:drink")

// Domain
include(":domain")

// Feature
include(":feature:search")
include(":feature:chat")
include(":feature:map")
include(":feature:profile")
include(":feature:home")
include(":feature:signin")
include(":feature:notifications")
include(":feature:edit_profile")
include(":feature:settings")
include(":feature:create_post")
include(":feature:create_note")
include(":feature:passcode")

project(":common").projectDir = File(settingsDir, "../common")
project(":ads").projectDir = File(settingsDir, "../ads")
project(":auth").projectDir = File(settingsDir, "../auth")

include(":feature:friend_request")
include(":feature:parties")
include(":feature:ticket")
include(":data:ticket")
include(":feature:friend_list")
include(":lint")
include(":feature:signup")
include(":feature:comment")
include(":data:comment")
include(":feature:post_likes")
include(":data:remote_config")
include(":data:search")
include(":data:map")
include(":data:chat")
include(":core:db")
