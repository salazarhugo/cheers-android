plugins {
    alias(libs.plugins.cheers.android.application)
    alias(libs.plugins.cheers.android.application.compose)
    alias(libs.plugins.cheers.android.application.firebase)
    alias(libs.plugins.cheers.android.room)
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.salazar.cheers"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.salazar.cheers"
        minSdk = 28
        targetSdk = 34
        versionCode = 72
        versionName = "1.0.0-072"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            versionNameSuffix = "_dev_debug"
            applicationIdSuffix = ".dev"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging.resources {
        excludes.add("/META-INF/AL2.0")
        excludes.add("/META-INF/LGPL2.1")
    }
}

dependencies {
    implementation(projects.ads)
    implementation(projects.common)

    implementation(projects.core.protobuf)
    implementation(projects.core.ui)
    implementation(projects.core.util)
    implementation(projects.core.model)
    implementation(projects.core.shared)
    implementation(projects.core.db)

    implementation(projects.feature.home)
    implementation(projects.feature.chat)
    implementation(projects.feature.map)
    implementation(projects.feature.search)
    implementation(projects.feature.profile)
    implementation(projects.feature.editProfile)
    implementation(projects.feature.signin)
    implementation(projects.feature.signup)
    implementation(projects.feature.notifications)
    implementation(projects.feature.settings)
    implementation(projects.feature.createPost)
    implementation(projects.feature.createNote)
    implementation(projects.feature.passcode)
    implementation(projects.feature.friendRequest)
    implementation(projects.feature.parties)
    implementation(projects.feature.ticket)
    implementation(projects.feature.friendList)
    implementation(projects.feature.comment)
    implementation(projects.feature.postLikes)

    implementation(projects.domain)

    implementation(projects.data.post)
    implementation(projects.data.user)
    implementation(projects.data.note)
    implementation(projects.data.party)
    implementation(projects.data.friendship)
    implementation(projects.data.activity)
    implementation(projects.data.story)
    implementation(projects.data.billing)
    implementation(projects.data.ticket)
    implementation(projects.data.comment)
    implementation(projects.data.drink)
    implementation(projects.data.remoteConfig)
    implementation(projects.data.search)
    implementation(projects.data.map)
    implementation(projects.data.chat)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.kotlinx.coroutines.play.services)

//    implementation(libs.grpc.okhttp)
//    implementation(libs.grpc.protobuf.lite) {
//        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
//    }
//    implementation(libs.grpc.stub)
//    implementation(libs.grpc.kotlin.stub)
//    compileOnly(libs.annotations.api) // necessary for Java 9+

//    implementation("com.google.protobuf:protobuf-javalite:3.21.12")

//    implementation("com.google.protobuf:protobuf-javalite:3.21.7"

    // KSP
    implementation(libs.symbol.processing.api)

    // Klaxon
    implementation(libs.klaxon)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)

    // Compose-State-Events
    implementation(libs.compose.state.events)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Jetpack Compose BOM
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.compose.runtime:runtime-livedata")

    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
//    implementation(libs.androidx.lifecycle.livedata.ktx)

    // OkHttp BOM
    implementation(platform(libs.okhttp3.bom))
    implementation(libs.okhttp3.okhttp)
    implementation(libs.okhttp3.logging.interceptor)

    // Moshi
    implementation(libs.converter.moshi)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Accompanist System UI Controller
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.placeholder.material)

    // Accompanist Pager
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    // Camera X
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.lifecycle)

    // Coil for Compose
    implementation(libs.coil.compose)

    // Coil SVG
    implementation(libs.coil.svg)

    // Google Ads
    implementation(libs.play.services.ads)

    // Google Billing
    implementation(libs.billing.ktx)

    // Constraint layout
    implementation(libs.androidx.constraintlayout)

    // Firebase BOM
    implementation(platform(libs.firebase.bom)) {
        exclude(group = "com.google.protobuf")
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }

    // App Check Play Integrity
    implementation("com.google.firebase:firebase-appcheck-playintegrity")

    // App Check Debug
    implementation(libs.firebase.appcheck.debug) {
        exclude(group = "com.google.protobuf")
    }

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation(libs.play.services.auth)

    //Firebase Dynamic-Links
    implementation("com.google.firebase:firebase-dynamic-links-ktx")

    // Firebase Firestore
//    implementation("com.google.firebase:firebase-firestore-ktx"

    // Firebase Functions
    implementation("com.google.firebase:firebase-functions-ktx") {
        exclude(group = "com.google.protobuf")
    }

    // Firebase Messaging
    implementation("com.google.firebase:firebase-messaging-ktx") {
        exclude(group = "com.google.protobuf")
    }

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx") {
        exclude(group = "com.google.protobuf")
    }

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.multiprocess)

    implementation(libs.play.services.maps)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // Paging
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Work Manager
    implementation(libs.androidx.work.runtime.ktx)

    // Snapchat
    implementation(libs.creativekit)
    implementation(libs.loginkit)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Swipe Refresh
    implementation(libs.accompanist.swiperefresh)

    // User Messaging Platform
    implementation(libs.user.messaging.platform)
    implementation(libs.androidx.animation.graphics)

    // Play In-App Update:
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)

    // <!------ Debugging ------!>
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

apply(plugin = "com.google.gms.google-services")