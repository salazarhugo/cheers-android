import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.firebase.crashlytics.gradlePlugin) {
        exclude(group = "com.google.protobuf")
    }
    compileOnly(libs.firebase.performance.gradlePlugin) {
        exclude(group = "com.google.protobuf")
    }
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "cheers.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "cheers.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("androidFeature") {
            id = "cheers.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("androidApplicationCompose") {
            id = "cheers.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("androidLibraryCompose") {
            id = "cheers.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("androidHilt") {
            id = "cheers.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("androidRoom") {
            id = "cheers.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }

        register("androidFirebase") {
            id = "cheers.android.application.firebase"
            implementationClass = "AndroidApplicationFirebaseConventionPlugin"
        }
    }
}