pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
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

// Core
include(":core:protobuf")
include(":core:ui")
include(":core:util")
include(":core:model")
include(":core:shared")
include(":core:analytics")

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
include(":feature:premium")

check(JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_17)) {
    """
    Cheers requires JDK 17+ but it is currently using JDK ${JavaVersion.current()}.
    Java Home: [${System.getProperty("java.home")}]
    https://developer.android.com/build/jdks#jdk-config-in-studio
    """.trimIndent()
}
include(":feature:drink")
